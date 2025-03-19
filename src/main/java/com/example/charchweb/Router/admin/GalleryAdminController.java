package com.example.charchweb.Router.admin;


import com.example.charchweb.DTO.photoAlbum;
import com.example.charchweb.DTO.photo;
import com.example.charchweb.service.photoAlbumService;
import com.example.charchweb.service.photoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/andio/gallery")
public class GalleryAdminController {

    @Autowired
    private photoAlbumService photoAlbumService;

    @Autowired
    private photoService photoService;

    // 앨범 목록 페이지
    @GetMapping("")
    public String listAlbums(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String category,
            Model model) {

        Page<photoAlbum> albums;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("eventDate").descending());

        if (keyword != null && !keyword.isEmpty()) {
            albums = photoAlbumService.searchAlbumsByKeyword(keyword, pageRequest);
        } else if (year != null && !year.isEmpty() && category != null && !category.isEmpty()) {
            albums = photoAlbumService.getAlbumsByYearAndCategory(Integer.parseInt(year), category, pageRequest);
        } else if (year != null && !year.isEmpty()) {
            albums = photoAlbumService.getAlbumsByYear(Integer.parseInt(year), pageRequest);
        } else if (category != null && !category.isEmpty()) {
            albums = photoAlbumService.getAlbumsByCategory(category, pageRequest);
        } else {
            albums = photoAlbumService.getAllAlbums(pageRequest);
        }

        model.addAttribute("albums", albums);
        model.addAttribute("years", photoAlbumService.getAllYears());
        model.addAttribute("categories", photoAlbumService.getAllCategories());

        return "admin/albumList";
    }

    // 앨범 상세 조회
    @GetMapping("/album/{id}")
    public String viewAlbum(@PathVariable Long id, Model model) {
        Optional<photoAlbum> albumOpt = photoAlbumService.getAlbumById(id);

        if (albumOpt.isEmpty()) {
            return "redirect:/andio/gallery";
        }

        photoAlbum album = albumOpt.get();
        List<photo> photos = photoService.getPhotosByAlbum(album);

        model.addAttribute("album", album);
        model.addAttribute("photos", photos);
        model.addAttribute("photoCount", photos.size());

        return "admin/albumDetail";
    }

    // 앨범 추가 폼
    @GetMapping("/album/add")
    public String albumAddForm(Model model) {
        photoAlbum album = new photoAlbum();
        album.setEventDate(LocalDate.now()); // 기본값으로 오늘 날짜 설정

        model.addAttribute("album", album);
        model.addAttribute("isNew", true);
        return "admin/albumDetail";
    }

    // 앨범 추가 처리
    @PostMapping("/album/add")
    public String addAlbum(
            @ModelAttribute photoAlbum album,
            @RequestParam(required = false) MultipartFile thumbnailFile,
            @RequestParam(required = false) MultipartFile[] albumPhotos,
            RedirectAttributes redirectAttributes) {

        try {
            // 1. 앨범 생성 (기존 코드)
            photoAlbum savedAlbum = photoAlbumService.createAlbum(album, thumbnailFile);

            // 2. 앨범 사진 추가 (추가된 부분)
            if (albumPhotos != null && albumPhotos.length > 0) {
                photoService.uploadPhotos(savedAlbum.getId(), Arrays.asList(albumPhotos));
            }

            redirectAttributes.addFlashAttribute("message", "앨범이 성공적으로 추가되었습니다.");
            return "redirect:/andio/gallery/album/" + savedAlbum.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "앨범 추가에 실패했습니다: " + e.getMessage());
            return "redirect:/andio/gallery/album/add";
        }
    }

    // 앨범 수정 폼
    @GetMapping("/album/{id}/edit")
    public String albumEditForm(@PathVariable Long id, Model model) {
        Optional<photoAlbum> albumOpt = photoAlbumService.getAlbumById(id);

        if (albumOpt.isEmpty()) {
            return "redirect:/andio/gallery";
        }

        model.addAttribute("album", albumOpt.get());
        model.addAttribute("isNew", false);

        return "admin/albumDetail";
    }

    // 앨범 수정 처리
    @PostMapping("/album/{id}/edit")
    public String updateAlbum(
            @PathVariable Long id,
            @ModelAttribute photoAlbum album,
            @RequestParam(required = false) MultipartFile thumbnailFile,
            @RequestParam(defaultValue = "false") boolean removeThumbnail,
            RedirectAttributes redirectAttributes) {

        try {
            // ID를 명시적으로 설정
            album.setId(id);
            photoAlbumService.updateAlbum(id, album, thumbnailFile, removeThumbnail);
            redirectAttributes.addFlashAttribute("message", "앨범이 성공적으로 수정되었습니다.");
            return "redirect:/andio/gallery/album/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "앨범 수정에 실패했습니다: " + e.getMessage());
            return "redirect:/andio/gallery/album/" + id + "/edit";
        }
    }

    // 앨범 삭제 처리
    @PostMapping("/album/delete/{id}")
    public String deleteAlbum(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            photoAlbumService.deleteAlbum(id);
            redirectAttributes.addFlashAttribute("message", "앨범이 성공적으로 삭제되었습니다.");
            return "redirect:/andio/gallery";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "앨범 삭제에 실패했습니다: " + e.getMessage());
            return "redirect:/andio/gallery/album/" + id;
        }
    }

    // 앨범 사진 관리 페이지
    @GetMapping("/album/{id}/photos")
    public String manageAlbumPhotos(@PathVariable Long id, Model model) {
        Optional<photoAlbum> albumOpt = photoAlbumService.getAlbumById(id);

        if (albumOpt.isEmpty()) {
            return "redirect:/andio/gallery";
        }

        photoAlbum album = albumOpt.get();
        List<photo> photos = photoService.getPhotosByAlbum(album);

        model.addAttribute("album", album);
        model.addAttribute("photos", photos);
        model.addAttribute("photoCount", photos.size());

        return "admin/albumPhotos";
    }

    // 사진 업로드 처리
    @PostMapping("/album/{id}/upload")
    public String uploadPhotos(
            @PathVariable Long id,
            @RequestParam("photoFiles") List<MultipartFile> files,
            RedirectAttributes redirectAttributes) {

        try {
            photoService.uploadPhotos(id, files);
            redirectAttributes.addFlashAttribute("message", "사진이 성공적으로 업로드되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "사진 업로드에 실패했습니다: " + e.getMessage());
        }

        return "redirect:/andio/gallery/album/" + id + "/photos";
    }

    // 사진 캡션 업데이트
    @PostMapping("/photo/{id}/update-caption")
    @ResponseBody
    public String updatePhotoCaption(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {

        try {
            photoService.updatePhotoCaption(id, payload.get("caption"));
            return "{\"success\": true}";
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }

    // 사진 삭제
    @PostMapping("/photo/{id}/delete")
    public String deletePhoto(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            Optional<photo> photoOpt = photoService.getPhotoById(id);
            if (photoOpt.isEmpty()) {
                throw new RuntimeException("사진을 찾을 수 없습니다.");
            }

            Long albumId = photoOpt.get().getAlbum().getId();
            photoService.deletePhoto(id);

            redirectAttributes.addFlashAttribute("message", "사진이 성공적으로 삭제되었습니다.");
            return "redirect:/andio/gallery/album/" + albumId + "/photos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "사진 삭제에 실패했습니다: " + e.getMessage());
            return "redirect:/andio/gallery";
        }
    }

    // 선택한 사진 삭제
    @PostMapping("/album/{id}/delete-selected")
    public String deleteSelectedPhotos(
            @PathVariable Long id,
            @RequestParam("photoIds") String photoIdsStr,
            RedirectAttributes redirectAttributes) {

        try {
            List<Long> photoIds = Arrays.stream(photoIdsStr.split(","))
                    .map(Long::parseLong)
                    .toList();

            photoService.deleteSelectedPhotos(id, photoIds);

            redirectAttributes.addFlashAttribute("message", photoIds.size() + "장의 사진이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "사진 삭제에 실패했습니다: " + e.getMessage());
        }

        return "redirect:/andio/gallery/album/" + id + "/photos";
    }

    // 사진 순서 변경
    @PostMapping("/album/{id}/reorder")
    @ResponseBody
    public String reorderPhotos(
            @PathVariable Long id,
            @RequestBody List<Long> photoIds) {

        try {
            photoService.updatePhotoOrder(photoIds);
            return "{\"success\": true}";
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }

    // 앨범 대표 이미지 설정
    @PostMapping("/album/{albumId}/set-thumbnail/{photoId}")
    @ResponseBody
    public String setAlbumThumbnail(
            @PathVariable Long albumId,
            @PathVariable Long photoId) {

        try {
            photoService.setAlbumThumbnail(albumId, photoId);
            return "{\"success\": true}";
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }
}