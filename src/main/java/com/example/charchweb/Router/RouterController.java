package com.example.charchweb.Router;

import com.example.charchweb.DTO.notice;
import com.example.charchweb.service.noticeService;
import com.example.charchweb.service.photoAlbumService;
import com.example.charchweb.DTO.Sermon;
import com.example.charchweb.DTO.SubMenu;
import com.example.charchweb.DTO.photoAlbum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Autowired
    private photoAlbumService photoAlbumService;

    @Autowired
    private com.example.charchweb.service.photoService photoService;

    @Autowired
    private noticeService noticeService;

    private String bannerURl = "https://images.unsplash.com/photo-1515162305285-0293e4767cc2?fm=jpg&q=60&w=3000&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8JUVBJUI1JTkwJUVEJTlBJThDfGVufDB8fDB8fHww";

    @GetMapping("/")
    public String index() {
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
                new SubMenu("예배시간", "/worship?menu=times", menu.equals("times"))
//               나중에 영상예배 추가시
//                new SubMenu("주일예배", "/worship?menu=sunday", menu.equals("sunday"))
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

    @GetMapping("/sermon/{id}")
    public String sermonDetail(@PathVariable Long id, Model model) {
        // 설교 상세 정보 가져오기
        Optional<Sermon> sermonOptional = sermonService.getSermonById(id);

        if (sermonOptional.isEmpty()) {
            return "redirect:/sermon"; // 설교가 없으면 목록으로 리다이렉트
        }

        Sermon sermon = sermonOptional.get();
        model.addAttribute("sermon", sermon);

        // 이전/다음 설교 가져오기
        Optional<Sermon> prevSermon = sermonService.getPreviousSermon(id);
        Optional<Sermon> nextSermon = sermonService.getNextSermon(id);

        prevSermon.ifPresent(s -> model.addAttribute("prevSermon", s));
        nextSermon.ifPresent(s -> model.addAttribute("nextSermon", s));

        // 관련 설교 (같은 설교자 또는 비슷한 성경 구절)
        List<Sermon> relatedSermons = sermonService.getRelatedSermons(sermon, 3);
        model.addAttribute("relatedSermons", relatedSermons);

        // 공통 모델 속성 설정
        model.addAttribute("bannerImage", bannerURl);
        model.addAttribute("pageTitle", "설교말씀");
        model.addAttribute("pageSubtitle", "생명의 말씀으로 은혜받는 시간이 되시기 바랍니다");

        model.addAttribute("section", "sermon");
        model.addAttribute("sectionTitle", "설교말씀");
        model.addAttribute("currentMenu", "recent");  // 일반적으로 최근 설교에서 상세로 들어올 것임

        // 사이드바 메뉴
        List<SubMenu> subMenus = Arrays.asList(
                new SubMenu("최근 설교", "/sermon?menu=recent", true),
                new SubMenu("설교 자료실", "/sermon?menu=archive", false)
        );
        model.addAttribute("subMenus", subMenus);

        return "sermonDetail";
    }

    @GetMapping("/news")
    public String news(
            @RequestParam(defaultValue = "notice") String menu,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<SubMenu> subMenus = Arrays.asList(
                new SubMenu("공지사항", "/news?menu=notice", menu.equals("notice")),
                new SubMenu("교회앨범", "/news?menu=gallery", menu.equals("gallery"))
        );

        // 공통 모델 속성 설정
        model.addAttribute("bannerImage", bannerURl);
        model.addAttribute("pageTitle", "교회 소식");
        model.addAttribute("pageSubtitle", "안디옥교회의 최신 소식을 확인하세요");
        model.addAttribute("section", "news");
        model.addAttribute("sectionTitle", "교회소식");
        model.addAttribute("subMenus", subMenus);
        model.addAttribute("currentMenu", menu);

        if (menu.equals("notice")) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("pinned").descending().and(Sort.by("createdAt").descending()));

            // 검색 키워드가 있는 경우
            Page<notice> noticePage;
            if (keyword != null && !keyword.isEmpty()) {
                noticePage = noticeService.searchNotices(keyword, pageable);
            } else {
                noticePage = noticeService.getAllNotices(pageable);
            }

            // 상단 고정 공지사항
            List<notice> pinnedNotices = noticeService.getPinnedNotices();

            // 메인 표시용 최신 공지
            Optional<notice> featuredNotice = noticeService.getLatestNotice();

            model.addAttribute("noticePage", noticePage);
            model.addAttribute("pinnedNotices", pinnedNotices);
            featuredNotice.ifPresent(notice -> model.addAttribute("featuredNotice", notice));
            model.addAttribute("keyword", keyword);
        }

        // menu에 따라 추가 데이터 로드
        if (menu.equals("gallery")) {
            // 갤러리 데이터 로드
            Page<photoAlbum> albums;
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("eventDate").descending());

            // 앨범 필터링 로직
            if (year != null && !year.isEmpty() && month != null && !month.isEmpty()) {
                // 연도와 월 필터링
                int yearValue = Integer.parseInt(year);
                int monthValue = Integer.parseInt(month);
                albums = photoAlbumService.getAlbumsByYearAndMonth(yearValue, monthValue, pageRequest);
            } else if (year != null && !year.isEmpty() && category != null && !category.isEmpty()) {
                // 연도와 카테고리 필터링
                albums = photoAlbumService.getAlbumsByYearAndCategory(Integer.parseInt(year), category, pageRequest);
            } else if (year != null && !year.isEmpty()) {
                // 연도 필터링
                albums = photoAlbumService.getAlbumsByYear(Integer.parseInt(year), pageRequest);
            } else if (category != null && !category.isEmpty()) {
                // 카테고리 필터링
                albums = photoAlbumService.getAlbumsByCategory(category, pageRequest);
            } else {
                // 필터 없음
                albums = photoAlbumService.getAllAlbums(pageRequest);
            }

            // 갤러리 관련 모델 속성 추가
            model.addAttribute("albums", albums);
            model.addAttribute("years", photoAlbumService.getAllYears());
            model.addAttribute("categories", photoAlbumService.getAllCategories());
            model.addAttribute("selectedYear", year);
            model.addAttribute("selectedMonth", month != null && !month.isEmpty() ? Integer.parseInt(month) : null);
            model.addAttribute("selectedCategory", category);
        }

        return "news";
    }

    @GetMapping("/news/album/{id}")
    public String newsAlbumDetail(@PathVariable Long id, Model model) {
        // 앨범 상세 정보 가져오기
        Optional<photoAlbum> albumOpt = photoAlbumService.getAlbumById(id);

        if (albumOpt.isEmpty()) {
            return "redirect:/news?menu=gallery"; // 앨범이 없으면 목록으로 리다이렉트
        }

        photoAlbum album = albumOpt.get();
        //사진이 없는경우
        if (album.getPhotos() == null) {
            album.setPhotos(new ArrayList<>());
        }

        model.addAttribute("album", album);

        // 이전/다음 앨범 가져오기
        photoAlbum prevAlbum = photoAlbumService.getPreviousAlbum(album.getEventDate(), album.getId());
        photoAlbum nextAlbum = photoAlbumService.getNextAlbum(album.getEventDate(), album.getId());

        model.addAttribute("prevAlbum", prevAlbum);
        model.addAttribute("nextAlbum", nextAlbum);

        // 공통 모델 속성 설정
        model.addAttribute("bannerImage", bannerURl);
        model.addAttribute("pageTitle", "교회 소식");
        model.addAttribute("pageSubtitle", "안디옥교회의 최신 소식을 확인하세요");

        // 사이드바 메뉴
        List<SubMenu> subMenus = Arrays.asList(
                new SubMenu("공지사항", "/news?menu=notice", false),
                new SubMenu("교회앨범", "/news?menu=gallery", true)
        );
        model.addAttribute("section", "news");
        model.addAttribute("sectionTitle", "교회소식");
        model.addAttribute("subMenus", subMenus);
        model.addAttribute("currentMenu", "gallery");

        return "albumDetail";
    }

    @GetMapping("/news/notice/{id}")
    public String noticeDetail(@PathVariable Long id, Model model) {
        Optional<notice> notice = noticeService.getNoticeById(id);

        if (notice.isPresent()) {
            // 조회수 증가
            noticeService.increaseViewCount(id);

            // 이전/다음 공지 가져오기
            Optional<notice> prevNotice = noticeService.getPreviousNotice(id);
            Optional<notice> nextNotice = noticeService.getNextNotice(id);

            model.addAttribute("notice", notice.get());
            prevNotice.ifPresent(prev -> model.addAttribute("prevNotice", prev));
            nextNotice.ifPresent(next -> model.addAttribute("nextNotice", next));

            // 관련 첨부파일
            model.addAttribute("attachments", noticeService.getAttachmentsByNoticeId(id));

            // 사이드바 메뉴 설정
            List<SubMenu> subMenus = Arrays.asList(
                    new SubMenu("공지사항", "/news?menu=notice", true),
                    new SubMenu("교회앨범", "/news?menu=gallery", false)
            );

            model.addAttribute("bannerImage", "https://images.unsplash.com/photo-1515162305285-0293e4767cc2");
            model.addAttribute("pageTitle", "교회 소식");
            model.addAttribute("pageSubtitle", "안디옥교회의 최신 소식을 확인하세요");
            model.addAttribute("section", "news");
            model.addAttribute("sectionTitle", "교회소식");
            model.addAttribute("subMenus", subMenus);
            model.addAttribute("currentMenu", "notice");

            return "noticeDetail";
        }

        return "redirect:/news?menu=notice";
    }
}