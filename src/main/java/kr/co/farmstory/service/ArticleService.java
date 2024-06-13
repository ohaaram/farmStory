package kr.co.farmstory.service;

import com.querydsl.core.Tuple;
import kr.co.farmstory.dto.ArticleDTO;
import kr.co.farmstory.dto.FileDTO;
import kr.co.farmstory.dto.PageRequestDTO;
import kr.co.farmstory.dto.PageResponseDTO;
import kr.co.farmstory.entity.Article;
import kr.co.farmstory.repository.ArticleRepository;
import kr.co.farmstory.repository.CommentRepository;
import kr.co.farmstory.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final FileRepository fileRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;

    @Value("${file.prodImg.path}")
    private String fileUploadPath;

    // 기본 글 목록 조회
    public PageResponseDTO selectArticles(PageRequestDTO pageRequestDTO){

        log.info("selectArticles...1");
        Pageable pageable = pageRequestDTO.getPageable("no");

        log.info("selectArticles...2");
        String cate = pageRequestDTO.getCate();

        Page<Tuple> pageArticles = articleRepository.selectArticles(pageRequestDTO, pageable);

        log.info("selectArticles...3" + pageArticles);

        // Page<Tuple>을 List<ArticleDTO>로 변환
        List<ArticleDTO> dtoList = pageArticles.getContent().stream()
                .map(tuple ->{
                    log.info("tuple : "+ tuple);
                    Article article = tuple.get(0, Article.class);
                    String nick = tuple.get(1, String.class);
                    article.setNick(nick);

                    log.info("article : "+ article);

                    return modelMapper.map(article, ArticleDTO.class);
                })
                .toList();
        log.info("selectArticles...4" +dtoList );
        int total = (int) pageArticles.getTotalElements();

        // List<articleDTO>와 page 정보 리턴
        return PageResponseDTO.builder()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total(total)
                .build();
    }

    // 검색 글 목록 조회
    public PageResponseDTO searchArticles(PageRequestDTO pageRequestDTO){
        log.info("키워드 검색 Serv 1 : " + pageRequestDTO.getKeyword());
        Pageable pageable = pageRequestDTO.getPageable("no");

        Page<Tuple> pageArticles = articleRepository.searchArticles(pageRequestDTO, pageable);

        List<ArticleDTO> dtoList = pageArticles.getContent().stream()
                .map(tuple ->{
                    log.info("키워드 검색 Serv 2 : " + tuple);
                    Article article = tuple.get(0, Article.class);
                    String nick = tuple.get(1, String.class);
                    article.setNick(nick);
                    log.info("키워드 검색 Serv 3 : "+ article);
                    return modelMapper.map(article, ArticleDTO.class);
                })
                .toList();

        int total = (int) pageArticles.getTotalElements();

        // List<articleDTO>와 page 정보 리턴
        return PageResponseDTO.builder()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total(total)
                .build();
    }

    // 글 상세 조회, 글 조회수 ++ 트랜잭션
    @Transactional
    public ArticleDTO selectArticleAndNick(int ano){
        Tuple optTuple = articleRepository.selectArticleAndNick(ano);
        log.info("selectArticle ... 1 : " + optTuple.toString());

        // Tuple -> Entity (Tuple -> DTO 변환은 불가)
        Article article = optTuple.get(0, Article.class);
        log.info("selectArticle ... 2 : " + article.toString());
        String nick = optTuple.get(1, String.class);
        log.info("selectArticle ... 3 nick : " + nick);
        article.setNick(nick);

        // Entity -> DTO
        ArticleDTO articleDTO = modelMapper.map(article, ArticleDTO.class);
        // Article hit ++
        articleRepository.incrementHitByAno(ano);
        return articleDTO;
    }

    // 글 수정 조회 : 글 수정은 조회수 변화 x
    public ArticleDTO selectArticleAndNickForModify(int ano){
        Tuple optTuple = articleRepository.selectArticleAndNick(ano);
        log.info("selectArticle ... 1 : " + optTuple.toString());

        // Tuple -> Entity (Tuple -> DTO 변환은 불가)
        Article article = optTuple.get(0, Article.class);
        log.info("selectArticle ... 2 : " + article.toString());
        String nick = optTuple.get(1, String.class);
        log.info("selectArticle ... 3 nick : " + nick);
        article.setNick(nick);

        // DTO -> Entity
        ArticleDTO articleDTO = modelMapper.map(article, ArticleDTO.class);
        return articleDTO;
    }

    // 게시판 글 작성
    public void insertArticle(ArticleDTO articleDTO){

        List<FileDTO> files = fileService.fileUpload(articleDTO);

        // 파일 개수
        articleDTO.setFile(files.size());

        Article article = modelMapper.map(articleDTO, Article.class);

        // 저장 후 저장한 엔티티 객체 반환(JPA sava() 메서드는 default로 저장한 Entity를 반환)
        Article saveArticle = articleRepository.save(article);
        log.info("insertArticle : " + saveArticle.toString());

        // 파일 insert
        int ano = saveArticle.getAno();
        fileService.insertFile(files, ano);

    }
    // 이벤트 글 작성
    public void registerEvent(ArticleDTO articleDTO, MultipartFile eventImg){
        log.info("이벤트 글 작성 Serv articleDTO : " + articleDTO.toString());
        log.info("이벤트 글 작성 Serv eventImg : " + eventImg);

        // 이벤트 정보 등록
        File file = new File(fileUploadPath);
        if (!file.exists()) {
            file.mkdir();
        }
        String path = file.getAbsolutePath();
        String sName = null;

        // 이미지 리사이즈 760 * height
        if(eventImg != null) {
            // oName, sName 구하기
            String oName = eventImg.getOriginalFilename();
            String ext = oName.substring(oName.lastIndexOf("."));
            sName = UUID.randomUUID().toString() + ext;
            log.info("insertReview oName : " + oName);
            log.info("insertReview sName : " + sName);

            try {
                String orgPath = path + "/orgImage";
                // 원본 파일 폴더 자동 생성
                java.io.File orgFile = new java.io.File(orgPath);
                if (!orgFile.exists()) {
                    orgFile.mkdir();
                }

                // 원본 파일 저장
                eventImg.transferTo(new File(orgPath, sName));
                // 리사이징 후 저장
                Thumbnails.of(new File(orgPath, sName)) // 원본 파일 (경로, 이름)
                        .width(750) // 원하는 사이즈
                        .toFile(new File(path, sName)); // 생성한 이미지 저장
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // 글 저장
        articleDTO.setFile(1);
        articleDTO.setThumbnail(sName);
        Article article = modelMapper.map(articleDTO, Article.class);
        // 저장 후 저장한 엔티티 객체 반환(JPA sava() 메서드는 default로 저장한 Entity를 반환)
        Article saveArticle = articleRepository.save(article);
        log.info("이벤트 글 작성 Serv 3 : " + saveArticle.toString());
    }
    // 글 수정
    @Transactional
    public ResponseEntity<?> updateArticle(ArticleDTO articleDTO){
        log.info("글 수정 Serv : " + articleDTO.toString());

        List<FileDTO> files = fileService.fileUpload(articleDTO);
        // 해당 게시글이 있는지 확인
        Article article = articleRepository.findById(articleDTO.getAno()).get();
        
        // 해당 게시글이 있을 경우
        if(article != null){
            // 변경한 글내용 삽입
            article.setContent(articleDTO.getContent());
            article.setTitle(articleDTO.getTitle());
            article.setFile(articleDTO.getFile());
            // 저장 후 저장한 엔티티 객체 반환
            Article updateArticle = articleRepository.save(article);
            log.info("updateArticle : " + updateArticle.toString());

            // 파일 insert
            int ano = articleDTO.getAno();
            fileService.insertFile(files, ano);

            return ResponseEntity.ok().body(updateArticle);
        }


        // 해당 게시글이 없을 경우
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");
    }

    // 글 삭제 + 해당 게시글의 댓글, 파일 삭제
    @Transactional
    public ResponseEntity<?> deleteArticle(int ano){

        log.info("글 삭제 Serv 1 : " + ano);
        // 해당 게시글이 있는지 확인
        Optional<Article> optArticle = articleRepository.findById(ano);
        log.info("글 삭제 Serv 2 : " + optArticle.toString());

        // 해당 게시글이 있으면
        if (optArticle.isPresent()){
            // 파일 삭제
            Article article = optArticle.get();
            if(article.getFile() > 0) {
                fileRepository.deleteFilesByAno(ano);
            }
            log.info("글 삭제 Serv 3 : " + article.toString());
            // 댓글 삭제
            commentRepository.deleteCommentByAno(ano);
            // 게시글 삭제
            articleRepository.deleteById(ano);

            return ResponseEntity.ok().body(optArticle.get());
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");
        }
    }
}
