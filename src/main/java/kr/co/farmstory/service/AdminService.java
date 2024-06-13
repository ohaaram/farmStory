package kr.co.farmstory.service;

import com.querydsl.core.Tuple;
import kr.co.farmstory.dto.*;
import kr.co.farmstory.entity.*;
import kr.co.farmstory.repository.ImagesRepository;
import kr.co.farmstory.repository.MarketRepository;
import kr.co.farmstory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminService {

    private final MarketRepository marketRepository;
    private final ImagesRepository imagesRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Value("${file.prodImg.path}")
    private String fileUploadPath;

    // 상품 등록
    public void productRegister(ProductDTO productDTO, MultipartFile thumb120, MultipartFile thumb240, MultipartFile thumb750) {

        log.info("파일 업로드 service1 productDTO : " + productDTO.toString());
        log.info("파일 업로드 service2 thumb120 : " + thumb120);
        log.info("파일 업로드 service3 thumb240 : " + thumb240);
        log.info("파일 업로드 service4 thumb750 : " + thumb750);

        // 상품 정보 등록 (정보 저장 & thumb120 저장)
        File file = new File(fileUploadPath);
        if (!file.exists()) {
            file.mkdir();
        }

        String path = file.getAbsolutePath();

        String orgPath = path + "/orgImage";
        // 원본 파일 폴더 자동 생성
        java.io.File orgFile = new java.io.File(orgPath);
        if(!orgFile.exists()){
            orgFile.mkdir();
        }

        // 저장
        Product savedProduct = new Product();

        if (!thumb120.isEmpty()) {
            // oName, sName 구하기
            String oName = thumb120.getOriginalFilename();
            String ext = oName.substring(oName.lastIndexOf("."));
            String sName = UUID.randomUUID().toString() + ext;
            log.info("파일 업로드 service5 oName : " + oName);
            log.info("파일 업로드 service6 sName : " + sName);

            try {
                // 원본 파일 저장
                thumb120.transferTo(new File(orgFile, sName));
                // 파일 이름 DTO에 저장
                productDTO.setThumb(sName);

                Thumbnails.of(new File(orgPath, sName)) // 원본 파일 (경로, 이름)
                        .size(120,120) // 원하는 사이즈
                        .toFile(new File(path, sName)); // 원본 파일 (경로, 이름)

                // 상품 정보 DB 저장
                Product product = modelMapper.map(productDTO, Product.class);
                log.info("파일 업로드 service7 product : " + product.toString());
                savedProduct = marketRepository.save(product);
                log.info("파일 업로드 service7 savedProduct : " + savedProduct.toString());
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        // 상품 이미지 등록
        Images savedImage = new Images();
        if (!thumb240.isEmpty() && !thumb750.isEmpty()) {
            // oName, sName 구하기
            String thumb240oName = thumb240.getOriginalFilename();
            String thumb240ext = thumb240oName.substring(thumb240oName.lastIndexOf("."));
            String thumb240sName = UUID.randomUUID().toString() + thumb240ext;

            String thumb750oName = thumb750.getOriginalFilename();
            String thumb750ext = thumb750oName.substring(thumb750oName.lastIndexOf("."));
            String thumb750sName = UUID.randomUUID().toString() + thumb750ext;

            log.info("파일 업로드 service8 thumb240sName : " + thumb240sName);
            log.info("파일 업로드 service9 thumb750sName : " + thumb750sName);

            try {
                // 원본 파일 저장
                thumb240.transferTo(new File(orgFile, thumb240sName));
                thumb750.transferTo(new File(orgFile, thumb750sName));
                // 이미지 이름 DTO에 저장
                ImagesDTO imagesDTO = ImagesDTO.builder()
                        .prodno(savedProduct.getProdno())
                        .thumb240(thumb240sName)
                        .thumb750(thumb750sName)
                        .build();

                Thumbnails.of(new File(orgPath, thumb240sName)) // 원본 파일 (경로, 이름)
                        .size(240,240) // 원하는 사이즈
                        .toFile(new File(path, thumb240sName)); // 원본 파일 (경로, 이름)

                Thumbnails.of(new File(orgPath, thumb750sName)) // 원본 파일 (경로, 이름)
                        .width(750) // 원하는 사이즈
                        .toFile(new File(path, thumb750sName)); // 원본 파일 (경로, 이름)

                // 이미지 정보 DB 저장
                Images image = modelMapper.map(imagesDTO, Images.class);
                log.info("파일 업로드 service10 image : " + image.toString());
                savedImage = imagesRepository.save(image);
                log.info("파일 업로드 service10 savedImage : " + savedImage.toString());


            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        log.info("파일 업로드 service11 끝");

    }

    //제품 목록을 조회
    public List<ProductDTO> products(){
        List<Product> products = productRepository.findAll();

        log.info("AdminService - products : "+products.toString());

        return products.stream().map(product -> modelMapper.map(product,ProductDTO.class))
                .collect(Collectors.toList());
    }

    // 사용자가 주문한 목록을 조회
    public OrderListResponseDTO orderList(PageRequestDTO pageRequestDTO){

        log.info("AdminService - orderList....1");

        Pageable pageable = pageRequestDTO.getPageable("orderNo");

        log.info("AdminService - orderList....2");

        Page<Tuple> orderList= marketRepository.orderList(pageRequestDTO,pageable);

        log.info("AdminService - orderList...3 : "+orderList.toString());

        List<OrderListDTO> orderListDTOS = orderList.getContent().stream()
                .map(tuple ->
                {
                    log.info("(tuple.get(0,Orders.class) : "+tuple.get(0,Integer.class));
                    log.info("tuple.get(1,Orders.class) : "+tuple.get(1,LocalDateTime.class));
                    log.info("tuple.get(2,User.class) : "+tuple.get(2,User.class));

                    Integer orderNo = tuple.get(0,Integer.class);
                    LocalDateTime rdate =tuple.get(1,LocalDateTime.class);
                    String user = tuple.get(2,String.class);
                    Integer count = tuple.get(3,Integer.class);
                    String prodName = tuple.get(4,String.class);
                    Integer price= tuple.get(5,Integer.class);
                    Integer delCost= tuple.get(6,Integer.class);
                    Integer amount = tuple.get(7,Integer.class);

                    OrderListDTO orderListDTO = new OrderListDTO();

                    orderListDTO.setOrderNO(orderNo);
                    orderListDTO.setRdate(rdate);
                    orderListDTO.setName(user);
                    orderListDTO.setCount(count);
                    orderListDTO.setProdname(prodName);
                    orderListDTO.setPrice(price);
                    orderListDTO.setDelCost(delCost);
                    orderListDTO.setAmount(amount);
                    orderListDTO.setSum(price*count);

                    return orderListDTO;
                }).toList();

        log.info("AdminService - orderList...4");

        int total = (int)orderList.getTotalElements();

        return OrderListResponseDTO.builder()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(orderListDTOS)
                .total(total)
                .build();
    }
}

