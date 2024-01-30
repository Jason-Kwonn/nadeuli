package kr.nadeuli.service.image.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import kr.nadeuli.dto.ImageDTO;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.NadeuliDeliveryDTO;
import kr.nadeuli.dto.OrikkiriDTO;
import kr.nadeuli.dto.SearchDTO;
import kr.nadeuli.entity.Image;
import kr.nadeuli.entity.NadeuliDelivery;
import kr.nadeuli.entity.Post;
import kr.nadeuli.entity.Product;
import kr.nadeuli.mapper.ImageMapper;
import kr.nadeuli.service.image.ImageRepository;
import kr.nadeuli.service.image.ImageService;
import kr.nadeuli.service.member.MemberService;
import kr.nadeuli.service.orikkirimanage.OrikkiriManageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Log4j2
@Transactional
@Service("imageServiceImpl")
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;
    private final MemberService memberService;
    private final OrikkiriManageService orikkiriManageService;
    @Value("${cloud.aws.s3.endpoint}")
    private String endpoint;

    @Value("${cloud.aws.credentials.bucket}")
    private String bucket;

    @Value("${cloud.aws.credentials.subDirectory1}")
    private String subDirectory1;

    @Value("${cloud.aws.credentials.subDirectory2}")
    private String subDirectory2;



    private final AmazonS3 amazonS3;

    @Value("${imageNum}")
    int imageNum;

    @Override
    public void addImage(List<MultipartFile> multipartFiles, ImageDTO imageDTO) {
        // forEach 구문을 통해 multipartFiles로 넘어온 파일들 하나씩 처리
        multipartFiles.forEach(file -> {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try (InputStream inputStream = file.getInputStream()) {
                // S3에 파일 업로드
                String subDirectory = isImageFile(getFileExtension(fileName)) ? subDirectory1 : isVideoFile(getFileExtension(fileName)) ? subDirectory2 : "";
                String s3ObjectKey = subDirectory + fileName;
                log.info("subDirectory:{}",subDirectory);
                log.info("s3ObjectKey:{}",s3ObjectKey);

                amazonS3.putObject(new PutObjectRequest(bucket, s3ObjectKey, inputStream, objectMetadata)
                                       .withCannedAcl(CannedAccessControlList.PublicRead));

                imageDTO.setImageName(generateImageUrl(fileName));
                imageRepository.save(imageMapper.imageDTOToImage(imageDTO));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
            }
        });
    }

    @Override
    public ImageDTO getImage(long imageId) throws Exception {
        return imageRepository.findById(imageId).map(imageMapper::imageToImageDTO).orElse(null);
    }

    @Override
    public List<ImageDTO> getImageList(long id, SearchDTO searchDTO) throws Exception {
        Pageable pageable = PageRequest.of(0, imageNum);
        Page<Image> imagePage;
        if(searchDTO.isPost())
            imagePage = imageRepository.findByPost(Post.builder().postId(id).build(), pageable);
        else if(searchDTO.isProduct())
            imagePage = imageRepository.findByProduct(Product.builder().productId(id).build(), pageable);
        else {
            imagePage = imageRepository.findByNadeuliDelivery(NadeuliDelivery.builder().nadeuliDeliveryId(id).build(), pageable);
        }
        log.info(imagePage);
        return imagePage.map(imageMapper::imageToImageDTO).toList();
    }

    @Override
    public void deletePostImage(long postId) throws Exception {
        log.info(postId);
        deleteEntityImages(Post.builder().postId(postId).build());
    }

    @Override
    public void deleteProductImage(long productId) throws Exception {
        log.info(productId);
        deleteEntityImages(Product.builder().productId(productId).build());
    }

    @Override
    public void deleteNadeuliDeliveryImage(long nadeuliDeliveryId) throws Exception {
        log.info(nadeuliDeliveryId);
        deleteEntityImages(NadeuliDelivery.builder().nadeuliDeliveryId(nadeuliDeliveryId).build());
    }


    @Override
    public void deleteImage(long imageId) throws Exception {
        log.info(imageId);
        imageRepository.deleteById(imageId);
    }

    @Override
    public void addProfile(MultipartFile profileImage, Object dto) {
        if (profileImage != null && !profileImage.isEmpty()) {
            String fileName = createFileName(profileImage.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(profileImage.getSize());
            objectMetadata.setContentType(profileImage.getContentType());

            try (InputStream inputStream = profileImage.getInputStream()) {
                // S3에 프로필 이미지 업로드
                String subDirectory = isImageFile(getFileExtension(fileName)) ? subDirectory1 : isVideoFile(getFileExtension(fileName)) ? subDirectory2 : "";
                String s3ObjectKey = subDirectory + fileName;
                log.info("subDirectory:{}", subDirectory);
                log.info("s3ObjectKey:{}", s3ObjectKey);

                amazonS3.putObject(new PutObjectRequest(bucket, s3ObjectKey, inputStream, objectMetadata)
                                       .withCannedAcl(CannedAccessControlList.PublicRead));

                // 프로필 이미지 URL 설정
                // 이 부분에서 이미지를 DB에 저장하거나 프로필 정보에 이미지 URL을 저장하는 등의 로직을 추가할 수 있습니다.
                if (dto instanceof MemberDTO) {
                    MemberDTO memberDTO = (MemberDTO) dto;
                    memberDTO.setPicture(endpoint+"/"+bucket+"/"+s3ObjectKey);
                    memberService.updateMember(memberDTO);

                }else if (dto instanceof OrikkiriDTO) {
                    OrikkiriDTO orikkiriDTO = (OrikkiriDTO) dto;
                    orikkiriDTO.setOrikkiriPicture(endpoint+"/"+bucket+"/"+s3ObjectKey);
                    orikkiriManageService.updateOrikkiri(orikkiriDTO);
                    // orikkiriDTO에 대한 처리를 추가할 수 있습니다.
                    // ...
                }

            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "프로필 이미지 업로드에 실패했습니다.");
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
        } else {
            // profileImage가 없을 경우 처리할 로직을 추가할 수 있습니다.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "프로필 이미지를 찾을 수 없습니다.");
        }
    }

    public void deleteImageByFileName(String fileName) {
        // endpoint와 bucket을 파일 경로에서 먼저 삭제
        String cleanedFileName = removeEndpointAndBucket(fileName);
        log.info("cleanedFileName,{}",cleanedFileName);
        // S3에서 파일 삭제
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, cleanedFileName));
    }

    public void deleteProfile(String fileName) {
        // 특정 조건을 만족하는 경우 파일 삭제를 수행하지 않음
        if (!fileName.equals("https://kr.object.ncloudstorage.com/nadeuli/image/nadeuli20231221213746683.png")) {
            // endpoint와 bucket을 파일 경로에서 먼저 삭제
            String cleanedFileName = removeEndpointForProfile(fileName);
            log.info("cleanedFileName, {}", cleanedFileName);
            // S3에서 파일 삭제
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, cleanedFileName));
        } else {
            log.info("해당 파일은 삭제되지 않습니다: {}", fileName);
        }
    }

    private void deleteEntityImages(Object entity) throws Exception {
        // 엔터티에 해당하는 이미지들을 가져옴
        Pageable pageable = PageRequest.of(0, imageNum);
        Page<Image> imagePage;

        if (entity instanceof Post) {
            imagePage = imageRepository.findByPost((Post) entity, pageable);
        } else if (entity instanceof Product) {
            imagePage = imageRepository.findByProduct((Product) entity, pageable);
        } else if (entity instanceof NadeuliDelivery) {
            imagePage = imageRepository.findByNadeuliDelivery((NadeuliDelivery) entity, pageable);
        } else {
            throw new IllegalArgumentException("해당하는 엔터티가없음.");
        }

        List<ImageDTO> imageDTOList = imagePage.map(imageMapper::imageToImageDTO).toList();

        // 각 이미지에 대해 Object Storage에서 삭제
        imageDTOList.forEach(imageDTO -> deleteImageByFileName(imageDTO.getImageName()));

        // 데이터베이스에서 삭제
        if (entity instanceof Post) {
            imageRepository.deleteByPost((Post) entity);
        } else if (entity instanceof Product) {
            imageRepository.deleteByProduct((Product) entity);
        } else {
            imageRepository.deleteByNadeuliDelivery((NadeuliDelivery) entity);
        }
    }

    private String createFileName(String fileName) {
        // 파일명에서 마지막 점을 기준으로 확장자 추출
        int lastDotIndex = fileName.lastIndexOf(".");

        if (lastDotIndex == -1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일명에 확장자가 없습니다.");
        }

        String fileExtension = fileName.substring(lastDotIndex);

        // 확장자를 제외한 파일명 가져오기
        String imageNameWithoutExtension = fileName.substring(0, lastDotIndex);

        // 현재 날짜 및 시간 정보 가져오기
        LocalDateTime now = LocalDateTime.now();

        // 날짜 및 시간 포맷 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

        // 이미지이름(확장자 제외) + 날짜 및 시간 + 확장자
        return imageNameWithoutExtension + now.format(formatter) + fileExtension;
    }

    // file 형식이 잘못된 경우를 확인하기 위해 만들어진 로직이며, 파일 타입과 상관없이 업로드할 수 있게 하기 위해 .의 존재 유무만 판단하였습니다.
    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }

    private String removeEndpointAndBucket(String fileName) {
        // endpoint와 bucket을 파일 경로에서 삭제
        String cleanedFileName = fileName.replaceFirst(endpoint + "/" + bucket + "/", "");

        // 파일명에서 '/image' 또는 '/video'를 제거
        cleanedFileName = removeSubDirectory(cleanedFileName);

        return cleanedFileName;
    }

    private String removeEndpointForProfile(String fileName) {
        // endpoint와 bucket을 파일 경로에서 삭제
        String cleanedFileName = fileName.replaceFirst(endpoint + "/" + bucket + "/", "");

        return cleanedFileName;
    }

    private String removeSubDirectory(String fileName) {
        // 파일명에서 '/image' 또는 '/video'를 제거
        if (fileName.startsWith(subDirectory1)) {
            return fileName.substring(subDirectory1.length());
        } else if (fileName.startsWith(subDirectory2)) {
            return fileName.substring(subDirectory2.length());
        }
        return fileName;
    }

    private String generateImageUrl(String fileName) {
        String extension = getFileExtension(fileName);
        String baseUrl = "https://kr.object.ncloudstorage.com/nadeuli/";

        // 이미지 디렉터리에는 .jpg와 .png, .gif, .jpeg, heif 확장자만 허용
        if (isImageFile(extension)) {
            return baseUrl + subDirectory1 + fileName;
        }
        // 비디오 디렉터리에는 .mp4 확장자만 허용
        else if (isVideoFile(extension)) {
            return baseUrl + subDirectory2 + fileName;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "지원되지 않는 파일 형식(" + extension + ") 입니다.");
        }
    }

    private boolean isImageFile(String extension) {
        return ".jpg".equalsIgnoreCase(extension) || ".png".equalsIgnoreCase(extension) || ".jpeg".equalsIgnoreCase(extension) || ".gif".equalsIgnoreCase(extension) || ".heif".equalsIgnoreCase(extension);
    }

    private boolean isVideoFile(String extension) {
        return ".mp4".equalsIgnoreCase(extension);
    }
}
