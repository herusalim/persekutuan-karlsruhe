package org.persekutuankarlsruhe.webapp.feedbacks;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.persekutuankarlsruhe.webapp.calendar.CalendarUtil;
import org.persekutuankarlsruhe.webapp.email.EmailSendFailedException;
import org.persekutuankarlsruhe.webapp.email.IEmailService;
import org.persekutuankarlsruhe.webapp.email.MailjetEmailService;
import org.persekutuankarlsruhe.webapp.sheets.JadwalPelayanan;
import org.persekutuankarlsruhe.webapp.sheets.Orang;
import org.persekutuankarlsruhe.webapp.sheets.SheetsDataProvider;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SendFeedbackController {

	private static final String SEMUA_PENGURUS = "Semua Pengurus";
	private String[] pengurus = { "Bobo", "Bravo", "Ferry", "Heru", "Limanan", "Erfia", "Nany", "Yanty" };
	private IEmailService emailService;
	private List<Orang> daftarOrang;
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

	private static final Logger LOG = Logger.getLogger(SendFeedbackController.class.getName());

	private List<Orang> getDaftarPenerima(String penerima) throws IOException {
		if (daftarOrang == null) {
			// Lazy loading
			daftarOrang = SheetsDataProvider.createProviderForPersekutuan().getDaftarOrangFromSheets();
		}

		String[] daftarNamaPenerima;
		if (SEMUA_PENGURUS.equals(penerima)) {
			daftarNamaPenerima = pengurus;
		} else {
			if (penerima.contains("(")) {
				penerima = penerima.substring(0, penerima.indexOf("(")).trim();
			}
			daftarNamaPenerima = new String[] { penerima };
		}
		return filterOrang(daftarOrang, daftarNamaPenerima);
	}

	@SuppressWarnings("unlikely-arg-type")
	private List<Orang> filterOrang(List<Orang> daftarOrang, String[] daftarNama) {
		List<Orang> result = new ArrayList<Orang>();
		for (String nama : daftarNama) {
			daftarOrang.stream().filter(element -> element.equals(nama)).forEach(element -> result.add(element));
		}
		return result;
	}

	@RequestMapping(value = "/feedback", method = RequestMethod.GET)
	public String sendQuestionForm(Model model) throws Exception {
		SheetsDataProvider dataProvider = SheetsDataProvider.createProviderForPersekutuan();

		List<JadwalPelayanan> daftarPelayanan = dataProvider.getDaftarJadwalPelayananFromSheetsFromBeginning();

		List<String> daftarPenerima = getNamaPembawaRenungan4MingguTerakhir(daftarPelayanan);
		daftarPenerima.add(SEMUA_PENGURUS);
		model.addAttribute("daftarPenerima", daftarPenerima);
		return "feedback/form";
	}

	private List<String> getNamaPembawaRenungan4MingguTerakhir(List<JadwalPelayanan> daftarPelayanan) {
		List<String> daftarPembawaRenungan = new ArrayList<String>();
		for (int i = 0; i > -4; i--) {
			int startDate = (i - 1) * 7;
			int endDate = i * 7;
			JadwalPelayanan jadwalDalamTujuhHari = CalendarUtil.getJadwalTerdekatDalamBbrpHari(daftarPelayanan,
					startDate, endDate);

			if (jadwalDalamTujuhHari != null) {
				LOG.info("Ditemukan pembawa renungan untuk Persekutuan " + jadwalDalamTujuhHari.getTanggal() + "\t"
						+ jadwalDalamTujuhHari.getPemimpinRenungan().get(0));
				daftarPembawaRenungan.add(jadwalDalamTujuhHari.getPemimpinRenungan().get(0).getNama() + " ("
						+ DATE_FORMAT.format(jadwalDalamTujuhHari.getTanggal()) + ")");
			} else {
				LOG.info("Tidak ditemukan untuk i = " + i + "\t" + startDate + "\t" + endDate);
			}
		}
		return daftarPembawaRenungan;
	}

	@RequestMapping(value = "/feedback", method = RequestMethod.POST)
	public String sendQuestion(HttpServletRequest request, Model model) throws Exception {

		String nama = request.getParameter("nama");
		String judul = request.getParameter("judul");
		String feedback = request.getParameter("feedback");
		String penerima = request.getParameter("penerima");

		LOG.info("Feedback: " + nama + "\t" + judul + "\t" + penerima + "\t" + feedback);
		String pengirim = (nama != null && !nama.isEmpty()) ? (nama + ": ") : "";
		final String subject = prepareSubject(judul, penerima, pengirim);

		getDaftarPenerima(penerima).forEach(orang -> {
			try {
				FeedbackDatastore.getInstance().addFeedback(nama, judul, feedback, orang.toString());
				getEmailService().sendEmail(subject, feedback, feedback.replace("\n", "<br/>"), Arrays.asList(orang));
			} catch (EmailSendFailedException e) {
				new RuntimeException(e);
			}
		});

		model.addAttribute("isSuccess", true);
		return "feedback/summary";
	}

	private String prepareSubject(String judul, String penerima, String pengirim) {
		final String subjectInfo = "[Feedback Persekutuan]";
		final String subject;
		String suffixTanggalPersekutuan;
		if (!SEMUA_PENGURUS.equals(penerima) && penerima.contains("(")) {
			String tanggalPersekutuan = penerima.substring(penerima.indexOf("(") + 1, penerima.indexOf(")"));
			suffixTanggalPersekutuan = " (Persekutuan tanggal " + tanggalPersekutuan + ")";
		} else {
			suffixTanggalPersekutuan = "";
		}
		if (judul != null && !judul.isEmpty()) {
			subject = subjectInfo + " " + pengirim + judul + suffixTanggalPersekutuan;
		} else {
			subject = subjectInfo + pengirim + "<tanpa judul>" + suffixTanggalPersekutuan;
		}
		return subject;
	}

//    @RequestMapping(value = "/admin/tampilPertanyaan")
//    public String showQuestionsForAdmin(Model model) throws Exception {
//        model.addAttribute("timeZone", CalendarUtil.getTimeZone());
//        model.addAttribute("daftarPertanyaan", getRelevantQuestions());
//        return "question/adminlist";
//    }

	public IEmailService getEmailService() {
		if (emailService == null) {
			emailService = MailjetEmailService.createEmailService();
		}
		return emailService;
	}

}
