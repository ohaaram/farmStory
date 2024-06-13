package kr.co.farmstory.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.farmstory.dto.ArticleDTO;
import kr.co.farmstory.dto.CommentDTO;
import kr.co.farmstory.dto.PageRequestDTO;
import kr.co.farmstory.dto.PageResponseDTO;
import kr.co.farmstory.service.ArticleService;
import kr.co.farmstory.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class CommunityController {

    private final ArticleService articleService;
    private final CommentService commentService;

    // 글 목록 조회
    @GetMapping("/community/newlist")
    public String community(Model model, String cate, PageRequestDTO pageRequestDTO){
        PageResponseDTO pageResponseDTO = null;

        if(pageRequestDTO.getKeyword() == null) {
            // 일반 글 목록 조회
            pageResponseDTO = articleService.selectArticles(pageRequestDTO);
        }else {
            // 검색 글 목록 조회
            log.info("키워드 검색 Cont" + pageRequestDTO.getKeyword());
            pageResponseDTO = articleService.searchArticles(pageRequestDTO);
        }
        log.info("pageResponseDTO : " + pageResponseDTO);

        model.addAttribute(pageResponseDTO);
        return "/community/newlist";
    }
    // 글 상세 보기
    @GetMapping("/community/newview")
    public String communityView(Model model, String cate, int ano, PageRequestDTO pageRequestDTO){
        log.info("communityView...1 : " + ano);
        // 글 조회
        ArticleDTO article = articleService.selectArticleAndNick(ano);

        // 댓글 조회
        List<CommentDTO> comments = commentService.selectComments(ano);
        log.info("communityView...2 : " + article.toString());

        // 페이지 정보 build
        PageResponseDTO pageResponseDTO = PageResponseDTO.builder()
                .pageRequestDTO(pageRequestDTO)
                .build();

        model.addAttribute("pageResponseDTO", pageResponseDTO);
        model.addAttribute("article", article);
        model.addAttribute("comments", comments);

        return "/community/newview";
    }
    // 글 쓰기
    @GetMapping("/community/newwrite")
    public String write(Model model, @ModelAttribute("cate") String cate, PageRequestDTO pageRequestDTO){

        PageResponseDTO pageResponseDTO = PageResponseDTO.builder()
                .pageRequestDTO(pageRequestDTO)
                .build();

        model.addAttribute(pageResponseDTO);
        return "/community/newwrite";
    }
    // 글 쓴거 보내기
    @PostMapping("/community/newwrite")
    public String write(ArticleDTO articleDTO){

        articleService.insertArticle(articleDTO);
        return "redirect:/community/newlist?cate="+articleDTO.getCate();
    }
    // 글 수정 - 글 상세 정보
    @GetMapping("/community/newmodify")
    public String modify(Model model, int ano, PageRequestDTO pageRequestDTO) {

        log.info("글 수정 글 조회 ...1 : " + ano);
        // 글 조회
        ArticleDTO article = articleService.selectArticleAndNickForModify(ano);

        log.info("글 수정 글 조회 ...2 : " + article.toString());
        // 페이지 정보 build
        PageResponseDTO pageResponseDTO = PageResponseDTO.builder()
                .pageRequestDTO(pageRequestDTO)
                .build();

        model.addAttribute("pageResponseDTO", pageResponseDTO);
        model.addAttribute("article", article);

        return "/community/newmodify";
    }
    // 글 수정
    @PostMapping("/community/newmodify")
    public String modify(ArticleDTO articleDTO, PageRequestDTO pageRequestDTO) {

        log.info("글 수정 Cont : " + articleDTO.toString());
        articleService.updateArticle(articleDTO);
        // 페이지 정보 build
        PageResponseDTO pageResponseDTO = PageResponseDTO.builder()
                .pageRequestDTO(pageRequestDTO)
                .build();

        // view로 리턴
        return "redirect:/community/newview?ano=" + articleDTO.getAno() + "&cate=" + articleDTO.getCate() + "&pg=" + pageRequestDTO.getPg();
    }
    // 글 삭제
    @GetMapping("/community/delete")
    public String deleteArticle(Model model, int ano, PageRequestDTO pageRequestDTO){
        log.info("글 삭제 Cont : " + ano);
        articleService.deleteArticle(ano);
        // 페이지 정보 build
        PageResponseDTO pageResponseDTO = PageResponseDTO.builder()
                .pageRequestDTO(pageRequestDTO)
                .build();
        log.info("글 삭제 Cont 2 : ");
        model.addAttribute("pageResponseDTO", pageResponseDTO);
        model.addAttribute("ano", ano);

        if(pageRequestDTO.getKeyword() == null){
            // 검색해서 들어온게 아니면
            return "redirect:/community/newlist?cate=" + pageRequestDTO.getCate() + "&pg=" + pageRequestDTO.getPg();
        }
        // 검색한 경우
        return "redirect:/community/newlist?cate=" + pageRequestDTO.getCate() + "&pg=" + pageRequestDTO.getPg()+ "&type=" + pageRequestDTO.getType()+ "&keyword=" + pageRequestDTO.getKeyword();
    }
}
