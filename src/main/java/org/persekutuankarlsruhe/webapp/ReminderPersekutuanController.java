package org.persekutuankarlsruhe.webapp;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.routines.EmailValidator;
import org.persekutuankarlsruhe.webapp.calendar.CalendarUtil;
import org.persekutuankarlsruhe.webapp.email.EmailSendFailedException;
import org.persekutuankarlsruhe.webapp.remindpersekutuan.RemindPersekutuanUtil;
import org.persekutuankarlsruhe.webapp.remindpersekutuan.Reminder;
import org.persekutuankarlsruhe.webapp.remindpersekutuan.ReminderPersekutuanDatastore;
import org.persekutuankarlsruhe.webapp.remindpersekutuan.ReminderPersekutuanRegister;
import org.persekutuankarlsruhe.webapp.remindpersekutuan.ReminderRegisterAlreadyExistException;
import org.persekutuankarlsruhe.webapp.remindpersekutuan.ReminderRegisterAlreadyExistException.RegisterType;
import org.persekutuankarlsruhe.webapp.sheets.JadwalPelayanan;
import org.persekutuankarlsruhe.webapp.sheets.Orang;
import org.persekutuankarlsruhe.webapp.sheets.SheetsDataProvider;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@Controller
public class ReminderPersekutuanController {

    private static final String ATTRIBUTE_SENT_REMINDERS = "sentReminders";
    private static final String ATTRIBUTE_INPUT_NAMA_ERROR = "inputNamaError";
    private static final String ATTRIBUTE_INPUT_EMAIL_ERROR = "inputEmailError";
    private static final String ATTRIBUTE_INPUT_SELECTIONS_ERROR = "inputSelectionsError";

    private static final Logger LOG = Logger.getLogger(ReminderPersekutuanController.class.getName());

    private static final String ATTRIBUTE_SUCCESS_MESSAGE = "successMessage";
    private static final String ATTRIBUTE_INFO_MESSAGE = "infoMessage";
    private static final String ATTRIBUTE_ERROR_MESSAGE = "errorMessage";
    private static final String REQUEST_PARAM_NAMA = "nama";
    private static final String REQUEST_PARAM_EMAIL = "email";
    private static final String REQUEST_PARAM_KEY = "key";
    private static final String REQUEST_PARAM_SELECTIONS = "selections";
    private static final String ATTRIBUTE_EMAIL = "email";
    private static final String ATTRIBUTE_REGISTER = "register";
    private static final String REQUEST_PARAM_BUTTON_UBAH = "ubah";
    private static final String REQUEST_PARAM_BUTTON_BATAL = "batal";
    private static final String REQUEST_PARAM_BUTTON_KIRIM = "kirim";
    private static final String REQUEST_PARAM_BUTTON_HAPUS = "hapus";

    RemindPersekutuanUtil util = new RemindPersekutuanUtil();

    @RequestMapping(value = "/reminder")
    public String reminderMain(HttpServletRequest request, Model model) throws Exception {
        handleUserStatus(request, model);
        return "reminder/home";
    }

    @RequestMapping(value = "/reminder/daftar", method = RequestMethod.GET)
    public String addReminderForm(HttpServletRequest request, Model model) throws Exception {

        handleUserStatus(request, model);

        return "reminder/add";
    }

    @RequestMapping(value = "/reminder/daftar", method = RequestMethod.POST)
    public String addReminderExecution(HttpServletRequest request, Model model) throws Exception {

        handleUserStatus(request, model);

        if (isValidInput(request, model)) {
            String nama = request.getParameter(REQUEST_PARAM_NAMA);
            String email = request.getParameter(REQUEST_PARAM_EMAIL);

            LOG.info("Berusaha mendaftarkan reminder untuk " + nama + " <" + email + ">");

            List<Integer> reminderList = new ArrayList<Integer>();
            for (String selection : request.getParameterValues(REQUEST_PARAM_SELECTIONS)) {
                reminderList.add(Integer.parseInt(selection));
            }
            try {

                ReminderPersekutuanDatastore.getInstance().addRegister(nama, email, reminderList);

                UserService userService = UserServiceFactory.getUserService();
                User currentUser = userService.getCurrentUser();
                if (currentUser != null && email.equalsIgnoreCase(currentUser.getEmail())) {
                    ReminderPersekutuanDatastore.getInstance().activateRegister(email);
                    model.addAttribute(ATTRIBUTE_SUCCESS_MESSAGE,
                            "Reminder untuk email " + email + " telah didaftarkan dan telah aktif.");
                    LOG.info("Reminder untuk " + nama + " <" + email + "> berhasil didaftarkan dan diaktifkan.");
                } else {
                    // Send activation email
                    util.sendEmailAktivasi(nama, email);
                    model.addAttribute(ATTRIBUTE_INFO_MESSAGE,
                            "Reminder berhasil didaftarkan, tapi belum diaktifkan. Cek inbox E-mail (" + email
                                    + ") untuk mengaktifkan reminder!");

                    LOG.info("Reminder untuk " + nama + " <" + email
                            + "> berhasil didaftarkan. Email aktivasi telah dikirim.");
                }
            } catch (ReminderRegisterAlreadyExistException e) {
                RegisterType registerType = e.getRegisterType();
                if (registerType == ReminderRegisterAlreadyExistException.RegisterType.ACTIVE) {
                    model.addAttribute(ATTRIBUTE_ERROR_MESSAGE, "Email " + email
                            + " telah didaftarkan sebelumnya dan telah aktif. Klik menu \"Ubah\" untuk mengubah reminder yang telah didaftarkan. Atau periksa inbox Email untuk mengaktifkan pendaftaran");

                    LOG.info("Gagal mendaftarkan untuk " + nama + " <" + email
                            + ">. Alasan: sudah terdaftar dan sudah aktif.");
                } else if (registerType == ReminderRegisterAlreadyExistException.RegisterType.INACTIVE) {

                    util.sendEmailAktivasi(nama, email);
                    model.addAttribute(ATTRIBUTE_INFO_MESSAGE,
                            "Reminder telah didaftarkan sebelumnya, tapi belum diaktifkan. Email aktivasi baru saja dikirimkan kembali. Cek inbox E-mail ("
                                    + email + ") untuk mengaktifkan reminder!");

                    LOG.info("Gagal mendaftarkan untuk " + nama + " <" + email
                            + ">. Alasan: Belum diaktifkan. Email aktivasi telah dikirim.");
                } else {
                    throw new IllegalArgumentException("Invalid Register Type: " + registerType);
                }
            }

        }
        return "reminder/add";
    }

    private boolean isValidInput(HttpServletRequest request, Model model) {

        StringBuffer errorMessage = new StringBuffer();

        if (StringUtils.isEmpty(request.getParameter(REQUEST_PARAM_NAMA))) {
            errorMessage.append("<li>Isi field Nama</li>");
            model.addAttribute(ATTRIBUTE_INPUT_NAMA_ERROR, true);
        }

        String email = request.getParameter(REQUEST_PARAM_EMAIL);
        if (StringUtils.isEmpty(email)) {
            errorMessage.append("<li>Isi field Email</li>");
            model.addAttribute(ATTRIBUTE_INPUT_EMAIL_ERROR, true);
        }

        if (!EmailValidator.getInstance().isValid(email)) {
            errorMessage.append("<li>Alamat email " + email + " tidak valid!</li>");
            model.addAttribute(ATTRIBUTE_INPUT_EMAIL_ERROR, true);
        }

        if (request.getParameter(REQUEST_PARAM_SELECTIONS) == null) {
            errorMessage.append("<li>Pilih reminder yang diinginkan!</li>");
            model.addAttribute(ATTRIBUTE_INPUT_SELECTIONS_ERROR, true);
        }
        if (errorMessage.length() > 0) {
            errorMessage.insert(0, "<ul>");
            errorMessage.append("</ul>");

            model.addAttribute(ATTRIBUTE_ERROR_MESSAGE, errorMessage);
            return false;
        } else {
            return true;
        }
    }

    @RequestMapping(value = "/reminder/ubah", method = RequestMethod.GET)
    public String editReminderForm(HttpServletRequest request, Model model) throws EmailSendFailedException {
        String view;
        User currentUser = handleUserStatus(request, model);
        String email = request.getParameter(REQUEST_PARAM_EMAIL);
        String key = request.getParameter(REQUEST_PARAM_KEY);

        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(key)) {
            // If the email or key parameter is empty, show the email field only
            view = tampilkanFormulirUbah(currentUser, model);
        } else {
            // request dari email link: show ubah form
            view = ubahMenggunakanLinkUbah(email, key, model);
        }

        return view;
    }

    private String tampilkanFormulirUbah(User currentUser, Model model) {
        String view;
        if (currentUser != null) {
            model.addAttribute(ATTRIBUTE_EMAIL, currentUser.getEmail());
            LOG.info("Request untuk mengubah reminder untuk Google account " + currentUser.getEmail()
                    + ". Tampilkan formulir untuk mengubah");
        }
        view = "reminder/maintainEnterEmail";
        return view;
    }

    private String ubahMenggunakanLinkUbah(String email, String key, Model model) throws EmailSendFailedException {
        String view;
        ReminderPersekutuanDatastore datastore = ReminderPersekutuanDatastore.getInstance();
        LOG.info("Request untuk mengubah reminder untuk " + email + " melalui link email.");
        try {
            Entity activeEntity = datastore.getActiveEntity(email);
            if (key.equals(datastore.getHashValue(activeEntity))) {
                ReminderPersekutuanRegister register = datastore.getReminderPersekutuanRegister(email);
                model.addAttribute(ATTRIBUTE_REGISTER, register);
                view = "reminder/maintainUbah";
                LOG.info("Tampilkan formulir untuk mengubah reminder untuk " + email + " (melalui link)");
            } else {
                model.addAttribute(ATTRIBUTE_ERROR_MESSAGE, "Link tidak valid");
                view = "reminder/maintainEnterEmail";
                LOG.info("Invalid key: " + key + " untuk email " + email + ".");
            }
        } catch (EntityNotFoundException e) {
            LOG.info("Mencoba mengirim aktivasi ke email " + email + " kalau perlu. Atau tampilkan error message.");
            trySendActivationEmailIfRegistered(model, email);
            view = "reminder/maintainEnterEmail";
        }
        return view;
    }

    @RequestMapping(value = "/reminder/ubah", method = RequestMethod.POST)
    public String editReminderExecution(HttpServletRequest request, Model model) throws Exception {
        String view;

        User currentUser = handleUserStatus(request, model);

        String email = request.getParameter(REQUEST_PARAM_EMAIL);

        if (request.getParameter(REQUEST_PARAM_BUTTON_HAPUS) != null) {
            view = hapusRegister(email, model);
        } else if (request.getParameter(REQUEST_PARAM_BUTTON_BATAL) != null) {
            view = "redirect:/reminder/ubah";
        } else if (request.getParameter(REQUEST_PARAM_BUTTON_KIRIM) != null) {
            // show form atau kirim link
            view = handleRequestPerubahanData(email, currentUser, model);
        } else if (request.getParameter(REQUEST_PARAM_BUTTON_UBAH) != null) {
            // simpan perubahan data
            view = prosesPerubahanData(email, request, model);
        } else {
            throw new IllegalStateException("POST request, tetapi tidak ada submit kirim ataupun ubah");
        }

        return view;
    }

    private String handleRequestPerubahanData(String email, User currentUser, Model model)
            throws EmailSendFailedException {
        String view;
        try {

            ReminderPersekutuanRegister register = ReminderPersekutuanDatastore.getInstance()
                    .getReminderPersekutuanRegister(email);
            if (currentUser != null && email.equalsIgnoreCase(currentUser.getEmail())) {
                model.addAttribute(ATTRIBUTE_REGISTER, register);
                view = "reminder/maintainUbah";
                LOG.info("Berusaha mengirim update data untuk " + email + " (Google Account)");
            } else {
                util.sendEmailUntukUbahPendaftaran(register);
                model.addAttribute(ATTRIBUTE_INFO_MESSAGE,
                        "Email konfirmasi untuk mengubah data telah dikirimkan, harap ikuti petunjuk di email yang dikirim ke alamat "
                                + email + " untuk mengubah reminder!");
                view = "reminder/maintainEnterEmail";
                LOG.info("Konfirmasi untuk ubah reminder untuk " + email + " telah dikirim (Bukan Google Account)");
            }

        } catch (EntityNotFoundException e) {
            trySendActivationEmailIfRegistered(model, email);
            view = "reminder/maintainEnterEmail";
        }
        return view;
    }

    private String prosesPerubahanData(String email, HttpServletRequest request, Model model)
            throws EntityNotFoundException, EmailSendFailedException {
        String view;
        LOG.info("Menerima pengubahan data untuk " + email);

        // process update
        String nama = request.getParameter(REQUEST_PARAM_NAMA);
        List<Integer> reminderList = new ArrayList<Integer>();
        String[] selections = request.getParameterValues(REQUEST_PARAM_SELECTIONS);
        if (isValidInput(request, model)) {
            for (String selection : selections) {
                reminderList.add(Integer.parseInt(selection));
            }

            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Mengubah data untuk Nama: " + nama + "\tEmail: " + email + "\treminderList: " + reminderList);
            }

            ReminderPersekutuanDatastore.getInstance().updateRegister(nama, email, reminderList);
            util.sendEmailStatusReminder(email);
            model.addAttribute(ATTRIBUTE_SUCCESS_MESSAGE,
                    "Data reminder untuk email " + email + " telah berhasil diubah");
            view = "reminder/maintainEnterEmail";
            LOG.info("Berhasil mengubah data untuk " + email);
        } else {
            model.addAttribute(ATTRIBUTE_REGISTER,
                    ReminderPersekutuanDatastore.getInstance().getReminderPersekutuanRegister(email));
            view = "reminder/maintainUbah";
        }
        return view;
    }

    private String hapusRegister(String email, Model model) throws EmailSendFailedException {
        String view;
        ReminderPersekutuanDatastore datastore = ReminderPersekutuanDatastore.getInstance();
        LOG.info("Mencoba menghapus data untuk email " + email);
        try {
            ReminderPersekutuanRegister register = datastore.getReminderPersekutuanRegister(email);
            datastore.deleteRegister(email);
            model.addAttribute(ATTRIBUTE_SUCCESS_MESSAGE,
                    "Data reminder untuk email " + email + " telah berhasil dihapus.");
            LOG.info("Berhasil menghapus data untuk email " + email);
            util.sendEmailHapusData(register);
            LOG.info("Email konfirmasi penghapusan data telah dikirim ke " + email);
        } catch (EntityNotFoundException e) {
            model.addAttribute(ATTRIBUTE_ERROR_MESSAGE,
                    "Tidak bisa menghapus reminder. Email " + email + " belum terdaftar.");
            LOG.info("Data untuk " + email + " tidak bisa dihapus, karena belum terdaftar");
        }
        view = "reminder/maintainEnterEmail";
        return view;
    }

    private void trySendActivationEmailIfRegistered(Model model, String email) throws EmailSendFailedException {
        ReminderPersekutuanDatastore datastore = ReminderPersekutuanDatastore.getInstance();
        try {
            Entity inactiveEntity = datastore.getInactiveEntity(email);
            util.sendEmailAktivasi(datastore.getName(inactiveEntity), email);
            model.addAttribute(ATTRIBUTE_INFO_MESSAGE,
                    "Email " + email + " belum diaktifkan. Periksa inbox untuk mengaktifkan reminder!");
            LOG.info("Email aktivasi untuk " + email + " terkirim.");
        } catch (EntityNotFoundException e1) {
            model.addAttribute(ATTRIBUTE_ERROR_MESSAGE, "Email " + email + " belum terdaftar");

            LOG.info("Email" + email + " belum terdaftar.");
        }
    }

    @RequestMapping(value = "/reminder/aktivasi")
    public String activate(HttpServletRequest request, Model model) {

        handleUserStatus(request, model);

        String email = request.getParameter(REQUEST_PARAM_EMAIL);
        String key = request.getParameter(REQUEST_PARAM_KEY);
        ReminderPersekutuanDatastore datastore = ReminderPersekutuanDatastore.getInstance();

        LOG.info("Mecoba mengaktifkan pendaftaran dengan email " + email + " dan key " + key);

        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(key)) {
            model.addAttribute(ATTRIBUTE_ERROR_MESSAGE, "Link aktivasi tidak valid");
        } else {
            try {
                Entity inactiveEntity = datastore.getInactiveEntity(email);
                String hashValue = datastore.getHashValue(inactiveEntity);
                if (key.equals(hashValue)) {
                    datastore.activateRegister(email);
                    model.addAttribute(ATTRIBUTE_SUCCESS_MESSAGE,
                            "Reminder untuk Email " + email + " telah berhasil diaktifkan.");
                    LOG.info("Berhasil mengaktifkan pendaftaran untuk " + email);
                } else {
                    model.addAttribute(ATTRIBUTE_ERROR_MESSAGE, "Link aktivasi tidak valid");
                    LOG.info("Link aktivasi untuk " + email + " tidak valid");
                }
            } catch (EntityNotFoundException e) {
                try {
                    datastore.getActiveEntity(email);
                    model.addAttribute(ATTRIBUTE_INFO_MESSAGE,
                            "Reminder untuk Email " + email + " sudah aktif sebelumnya");
                    LOG.info("Pendaftaran untuk email " + email + " sudah aktif sebelumnya");
                } catch (EntityNotFoundException e1) {
                    model.addAttribute(ATTRIBUTE_ERROR_MESSAGE, "Email " + email + " belum terdaftar");
                    LOG.info("Pendaftaran untuk email " + email + " belum terdaftar");
                }
            }
        }
        return "reminder/activationStatus";
    }

    private User handleUserStatus(HttpServletRequest request, Model model) {
        String thisUrl = request.getRequestURI();
        UserService userService = UserServiceFactory.getUserService();
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("logoutURL", userService.createLogoutURL(thisUrl));
        } else {
            model.addAttribute("loginURL", userService.createLoginURL(thisUrl));
        }

        return currentUser;
    }

    @RequestMapping(value = "/tasks/remindpersekutuan")
    public String remindPersekutuan(Model model) throws Exception {

        List<Reminder> sentReminders = new ArrayList<Reminder>();
        SheetsDataProvider dataProvider = SheetsDataProvider.createProviderForPersekutuan();

        List<JadwalPelayanan> daftarJadwal = dataProvider.getDaftarJadwalPelayananFromSheets();
        List<ReminderPersekutuanRegister> allRegisters = ReminderPersekutuanDatastore.getInstance().getAllRegisters();

        LOG.info("Mencoba mengirimkan reminder persekutuan. Jadwal: " + daftarJadwal + "\tDaftar Registers:"
                + allRegisters);

        for (ReminderPersekutuanRegister register : allRegisters) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Memeriksa apakah perlu mengirimkan reminder untuk register berikut: " + register);
            }
            for (Number offset : register.getReminderList()) {
                int offsetHari = offset.intValue();
                // Reminder dikirim jam 8, acara persekutuan pembanding jam
                // 00:00, jadi antara offsetHari-1 dengan offsetHari
                JadwalPelayanan jadwalUntukDiingatkan = CalendarUtil.getJadwalTerdekatDalamBbrpHari(daftarJadwal,
                        offsetHari - 1, offsetHari);
                if (jadwalUntukDiingatkan != null) {
                    Orang orang = new Orang(register.getNama(), register.getEmail());
                    LOG.info("Mengirim reminder ke " + orang + " untuk jadwal " + jadwalUntukDiingatkan);
                    util.sendReminderPersekutuan(orang, jadwalUntukDiingatkan, offsetHari);
                    sentReminders.add(new Reminder(orang, jadwalUntukDiingatkan.getTanggal()));
                }
            }
        }
        model.addAttribute(ATTRIBUTE_SENT_REMINDERS, sentReminders);
        return "reminder/reminderPersekutuanSummary";
    }
}
