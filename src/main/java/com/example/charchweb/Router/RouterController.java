package com.example.charchweb.Router;

import com.example.charchweb.DTO.Sermon;
import com.example.charchweb.DTO.SubMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.data.domain.Pageable;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class RouterController {

    @Autowired
    private com.example.charchweb.service.sermonService sermonService;

    private String bannerURl = "https://images.unsplash.com/photo-1515162305285-0293e4767cc2?fm=jpg&q=60&w=3000&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8JUVBJUI1JTkwJUVEJTlBJThDfGVufDB8fDB8fHww";
    @GetMapping("/")
    public String index(){
     return "home";
    }

    @GetMapping("/about")
    public String about(@RequestParam(defaultValue = "greeting") String menu, Model model) {
        List<SubMenu> subMenus = Arrays.asList(
                new SubMenu("인사말", "/about?menu=greeting", menu.equals("greeting")),
                new SubMenu("섬기는 분들", "/about?menu=pastors", menu.equals("pastors")),
                new SubMenu("후원 및 선교사/단체", "/about?menu=mission", menu.equals("mission")),
                new SubMenu("오시는 길", "/about?menu=location", menu.equals("location"))
        );
        model.addAttribute("bannerImage", bannerURl);
        model.addAttribute("pageTitle", "교회 소개");
        model.addAttribute("pageSubtitle", "안디옥교회를 소개합니다");
        model.addAttribute("currentMenu", "greeting");

        model.addAttribute("section", "about");
        model.addAttribute("sectionTitle", "교회소개");
        model.addAttribute("subMenus", subMenus);
        model.addAttribute("currentMenu", menu);

        return "aboutPage/about";
    }
    @GetMapping("/worship")
    public String worship(@RequestParam(defaultValue = "times") String menu, Model model) {
        List<SubMenu> subMenus = Arrays.asList(
                new SubMenu("예배시간", "/worship?menu=times", menu.equals("times")),
                new SubMenu("주일예배", "/worship?menu=sunday", menu.equals("sunday"))
        );
        model.addAttribute("bannerImage", bannerURl);
        model.addAttribute("pageTitle", "예배 안내");
        model.addAttribute("pageSubtitle", "안디옥교회의 예배 시간과 장소를 안내합니다");
        model.addAttribute("currentMenu", "worship");

        model.addAttribute("section", "worship");
        model.addAttribute("sectionTitle", "예배안내");
        model.addAttribute("subMenus", subMenus);
        model.addAttribute("currentMenu", menu);

        return "worship";
    }
    @GetMapping("/sermon")
    public String sermon(
            @RequestParam(defaultValue = "recent") String menu,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<SubMenu> subMenus = Arrays.asList(
                new SubMenu("최근 설교", "/sermon?menu=recent", menu.equals("recent")),
                new SubMenu("설교 자료실", "/sermon?menu=archive", menu.equals("archive"))
        );
        model.addAttribute("bannerImage", bannerURl);
        model.addAttribute("pageTitle", "설교말씀");
        model.addAttribute("pageSubtitle", "생명의 말씀으로 은혜받는 시간이 되시기 바랍니다");
        model.addAttribute("currentMenu", "sermon");

        model.addAttribute("section", "sermon");
        model.addAttribute("sectionTitle", "설교말씀");
        model.addAttribute("subMenus", subMenus);
        model.addAttribute("currentMenu", menu);

        // menu에 따라 필요한 데이터 추가
        if (menu.equals("recent")) {
            // 최근 설교 데이터
            Optional<Sermon> latestSermon = sermonService.getLatestSermon();
            latestSermon.ifPresent(sermon -> model.addAttribute("latestSermon", sermon));

            List<Sermon> recentSermons = new ArrayList<>();
            if (latestSermon.isPresent()) {
                recentSermons = sermonService.getRecentSermons(5);
            }
            model.addAttribute("recentSermons", recentSermons);
        } else if (menu.equals("archive")) {
            // 설교 자료실 데이터 (페이지네이션 포함)
            Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

            Page<Sermon> sermonsPage = (keyword != null && !keyword.isEmpty())
                    ? sermonService.searchSermons(keyword, pageable)
                    : sermonService.getAllSermons(pageable);

            model.addAttribute("sermonsPage", sermonsPage);
            model.addAttribute("keyword", keyword);
        }

        return "sermon";
    }
    @GetMapping("/news")
    public String news(@RequestParam(defaultValue = "notice") String menu, Model model) {
        List<SubMenu> subMenus = Arrays.asList(
                new SubMenu("공지사항", "/news?menu=notice", menu.equals("notice")),
                new SubMenu("교회앨범", "/news?menu=events", menu.equals("events"))
        );

        model.addAttribute("bannerImage", bannerURl);
        model.addAttribute("pageTitle", "교회 소식");
        model.addAttribute("pageSubtitle", "안디옥교회의 최신 소식을 확인하세요");
        model.addAttribute("currentMenu", "notice");

        model.addAttribute("section", "news");
        model.addAttribute("sectionTitle", "교회소식");
        model.addAttribute("subMenus", subMenus);
        model.addAttribute("currentMenu", menu);

        return "news";
    }


}
