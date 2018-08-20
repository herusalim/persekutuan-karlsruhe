package org.persekutuankarlsruhe.webapp.sendquestions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.persekutuankarlsruhe.webapp.ReminderPelayananController;
import org.persekutuankarlsruhe.webapp.calendar.CalendarUtil;
import org.persekutuankarlsruhe.webapp.email.IEmailService;
import org.persekutuankarlsruhe.webapp.email.MailjetEmailService;
import org.persekutuankarlsruhe.webapp.sheets.Orang;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SendQuestionController {
    private static final long FOURTEEN_DAYS_IN_MILLIS = TimeUnit.DAYS.toMillis(14);

    private IEmailService emailService;

    private static final Logger LOG = Logger.getLogger(ReminderPelayananController.class.getName());

    private static final SimpleDateFormat DATE_FORMAT_SELESAI = new SimpleDateFormat("yyyy-MM-dd");

    @RequestMapping(value = "/kirimPertanyaan", method = RequestMethod.GET)
    public String sendQuestionForm(Model model) throws Exception {
        return "question/form";
    }

    @RequestMapping(value = "/kirimPertanyaan", method = RequestMethod.POST)
    public String sendQuestion(HttpServletRequest request, Model model) throws Exception {

        String nama = request.getParameter("nama");
        String pertanyaan = request.getParameter("pertanyaan");
        String parameterTampilkan = request.getParameter("tampilkan");
        boolean tampilkan = parameterTampilkan != null ? Boolean.parseBoolean(parameterTampilkan) : false;
        LOG.info("Pertanyaan: " + pertanyaan + "\t" + nama + "\t" + tampilkan);
        QuestionDatastore.getInstance().addQuestion(nama, pertanyaan, tampilkan);
        getEmailService().sendEmail("Pertanyaan Persekutuan", pertanyaan, pertanyaan.replace("\n", "<br/>"),
                Arrays.asList(new Orang("Heru", "herumartinus.salim@yahoo.de")));

        model.addAttribute("isSuccess", true);
        return "question/summary";
    }

    @RequestMapping(value = "/tampilPertanyaan")
    public String showQuestions(Model model) throws Exception {
        model.addAttribute("timeZone", CalendarUtil.getTimeZone());
        model.addAttribute("daftarPertanyaan", getRelevantQuestions());
        return "question/list";
    }

    private List<Question> getRelevantQuestions() {
        List<Question> questions = QuestionDatastore.getInstance().getQuestions();
        List<Question> shownQuestions = new ArrayList<Question>();
        for (Question question : questions) {
            if (question.isShowPublic()) {
                long timestampSelesai = question.getTimestampSelesai();
                if (timestampSelesai == 0 || System.currentTimeMillis() - timestampSelesai < FOURTEEN_DAYS_IN_MILLIS) {
                    shownQuestions.add(question);
                }
            }
        }
        return shownQuestions;
    }

    @RequestMapping(value = "/admin/tampilPertanyaan")
    public String showQuestionsForAdmin(Model model) throws Exception {
        model.addAttribute("timeZone", CalendarUtil.getTimeZone());
        model.addAttribute("daftarPertanyaan", getRelevantQuestions());
        return "question/adminlist";
    }

    @RequestMapping(value = "/admin/ubahPertanyaan")
    public String updateQuestionsForAdmin(Model model, HttpServletRequest request) throws Exception {
        List<Question> questions = QuestionDatastore.getInstance().getQuestions();
        for (Question question : questions) {
            String tanggalSelesai = request.getParameter("tanggalSelesai_" + question.getTimestamp());
            if (tanggalSelesai != null && !tanggalSelesai.equals("")) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(CalendarUtil.getTimeZone());
                calendar.setTime(DATE_FORMAT_SELESAI.parse(tanggalSelesai));
                question.setTimestampSelesai(calendar.getTimeInMillis());
            } else {
                question.setTimestampSelesai(0);
            }
        }
        QuestionDatastore.getInstance().updateQuestions(questions);

        return "redirect:/admin/tampilPertanyaan";
    }

    public IEmailService getEmailService() {
        if (emailService == null) {
            emailService = MailjetEmailService.createEmailService();
        }
        return emailService;
    }

}
