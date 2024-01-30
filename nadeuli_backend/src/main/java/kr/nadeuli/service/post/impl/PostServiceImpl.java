package kr.nadeuli.service.post.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nadeuli.dto.PostDTO;
import kr.nadeuli.dto.SearchDTO;
import kr.nadeuli.dto.StreamingDTO;
import kr.nadeuli.entity.Post;
import kr.nadeuli.mapper.PostMapper;
import kr.nadeuli.service.post.PostRepository;
import kr.nadeuli.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;


@RequiredArgsConstructor
@Log4j2
@Transactional
@Service("postServiceImpl")
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;


    @Override
    public Long addPost(PostDTO postDTO) throws Exception {
        Post post = postMapper.postDTOToPost(postDTO);
        log.info(post);
        Post savedPost = postRepository.save(post);
        return savedPost.getPostId();
    }

    @Override
    public PostDTO getPost(long postId) throws Exception {
        return postRepository.findById(postId).map(postMapper::postToPostDTO).orElse(null);
    }

    @Override
    public List<PostDTO> getPostList(String gu, SearchDTO searchDTO) throws Exception {
        Sort sort = Sort.by(Sort.Direction.DESC, "regDate");
        Pageable pageable = PageRequest.of(searchDTO.getCurrentPage(), searchDTO.getPageSize(), sort);
        Page<Post> postPage;
        if(searchDTO.getSearchKeyword() == null || searchDTO.getSearchKeyword().isEmpty()){
            postPage = postRepository.findPostList(gu, pageable);
        }else {
            postPage = postRepository.findPostListByKeyword(searchDTO.getSearchKeyword(), searchDTO.getSearchKeyword(), gu, pageable);
        }
        log.info(postPage);
        System.out.println(postPage);
        return postPage.map(postMapper::postToPostDTO).toList();
    }

    @Override
    public Long updatePost(PostDTO postDTO) throws Exception {
        Post post = postMapper.postDTOToPost(postDTO);
        log.info(post);
        Post savedPost = postRepository.save(post);
        return savedPost.getPostId();
    }

    @Override
    public void deletePost(long postId) throws Exception {
        log.info(postId);
        postRepository.deleteById(postId);
    }



    @Override
    public StreamingDTO addStreamingChannel(StreamingDTO streamingDTO) throws Exception {
        String time = String.valueOf(System.currentTimeMillis());
        String addChannelName = streamingDTO.getChannelName();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time);
        headers.set("x-ncp-iam-access-key", accessKey);
        headers.set("x-ncp-apigw-signature-v2", makeSignaturePost(time));

        // JSON 본문을 문자열로
        String jsonBody = "{\"channelName\": \""+addChannelName+"\"," +
                "\"outputProtocol\": \"HLS,DASH\", " +
                "\"cdn\": " +
                "{\"createCdn\": false, " +
                "\"cdnType\": \"GLOBAL_EDGE\"," +
                "\"cdnInstanceNo\": 1827}," +
                "\"qualitySetId\": 3," +
                "\"useDvr\": true," +
                "\"immediateOnAir\": true," +
                "\"timemachineMin\": 360, " +
                "\"envType\": \"REAL\", " +
                "\"record\": " +
                "{\"type\": \"AUTO_UPLOAD\", " +
                "\"format\": \"ALL\", " +
                "\"bucketName\": \"nadeuli\", " +
                "\"filePath\": \"/streaming\", " +
                "\"accessControl\": \"PUBLIC_READ\"}, " +
                "\"isStreamFailOver\": false, " +
                "\"drmEnabledYn\": false}";

        // 헤더와 본문을 포함하는 HttpEntity 객체 생성
        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

        // RestTemplate 객체를 생성하고 POST 요청을 보냄
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://livestation.apigw.ntruss.com/api/v2/channels";
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        // JSON 응답을 파싱하여 필요한 정보 추출
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());
        JsonNode contentNode = rootNode.path("content");
        String channelId = contentNode.path("channelId").asText();
        StreamingDTO fetchedStreamingDTO = getStreamingChannel(channelId);
        return fetchedStreamingDTO;
    }

    @Override
    public StreamingDTO getStreamingChannel(String channelId) throws Exception {
        String time = String.valueOf(System.currentTimeMillis());

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time);
        headers.set("x-ncp-iam-access-key", accessKey);
        headers.set("x-ncp-apigw-signature-v2", makeSignatureGet(time, channelId));

        // HttpEntity 객체 생성 (헤더만 포함)
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // RestTemplate 객체를 생성하고 GET 요청을 보냄
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://livestation.apigw.ntruss.com/api/v2/channels/" + channelId;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        JsonNode contentNode = rootNode.path("content");

        StreamingDTO streamingUrlDTO = getStreamingUrl(channelId);

        return StreamingDTO.builder()
                .channelId(contentNode.path("channelId").asText())
                .channelName(contentNode.path("channelName").asText())
                .rtmpUrl(contentNode.path("publishUrl").asText())
                .globalRtmpUrl(contentNode.path("globalPublishUrl").asText())
                .channelStatus(contentNode.path("channelStatus").asText())
                .streamKey(contentNode.path("streamKey").asText())
                .url(streamingUrlDTO.getUrl())
                .build();
    }

    public StreamingDTO getStreamingUrl(String channelId) throws Exception {
        String time = String.valueOf(System.currentTimeMillis());

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time);
        headers.set("x-ncp-iam-access-key", accessKey);
        headers.set("x-ncp-apigw-signature-v2", makeSignatureUrl(time, channelId));

        // HttpEntity 객체 생성 (헤더만 포함)
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // RestTemplate 객체를 생성하고 GET 요청을 보냄
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://livestation.apigw.ntruss.com/api/v2/channels/"+channelId+"/serviceUrls?serviceUrlType=GENERAL";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        JsonNode contentArray = rootNode.path("content");

        // 'ABR' 이름을 가진 객체 찾기
        JsonNode abrNode = null;
        for (JsonNode node : contentArray) {
            if ("ABR".equals(node.path("name").asText())) {
                abrNode = node;
                break;
            }
        }

        // 'ABR' 객체에서 URL 추출
        String abrUrl = abrNode != null ? abrNode.path("url").asText() : null;

        return StreamingDTO.builder()
                .url(abrUrl) // 'ABR' 객체의 URL 사용
                .build();
    }


    @Override
    public void deleteStreamingChannel(String channelId) throws Exception {
        String time = String.valueOf(System.currentTimeMillis());

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time);
        headers.set("x-ncp-iam-access-key", accessKey);
        headers.set("x-ncp-apigw-signature-v2", makeSignatureDelete(time, channelId));

        // HttpEntity 객체 생성 (헤더만 포함)
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // RestTemplate 객체를 생성하고 GET 요청을 보냄
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://livestation.apigw.ntruss.com/api/v2/channels/" + channelId;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    public String makeSignaturePost(String time) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        String space = " ";					// 공백
        String newLine = "\n";  				// 줄바꿈
        String method = "POST";  				// HTTP 메서드
        String url = "/api/v2/channels";	// 도메인을 제외한 "/" 아래 전체 url (쿼리스트링 포함)
        String timestamp = time;		// 현재 타임스탬프 (epoch, millisecond)

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);

        return encodeBase64String;
    }

    public String makeSignatureGet(String time, String channelId) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String space = " ";					// 공백
        String newLine = "\n";  				// 줄바꿈
        String method = "GET";  				// HTTP 메서드
        String url = "/api/v2/channels/"+ channelId;	// 도메인을 제외한 "/" 아래 전체 url (쿼리스트링 포함)
        String timestamp = time;		// 현재 타임스탬프 (epoch, millisecond)

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);

        System.out.println(timestamp);
        System.out.println(encodeBase64String);

        return encodeBase64String;
    }

    public String makeSignatureUrl(String time, String channelId) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String space = " ";					// 공백
        String newLine = "\n";  				// 줄바꿈
        String method = "GET";  				// HTTP 메서드
        String url = "/api/v2/channels/"+channelId+"/serviceUrls?serviceUrlType=GENERAL";	// 도메인을 제외한 "/" 아래 전체 url (쿼리스트링 포함)
        String timestamp = time;		// 현재 타임스탬프 (epoch, millisecond)

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);

        System.out.println("url : "+ timestamp);
        System.out.println("url : "+encodeBase64String);

        return encodeBase64String;
    }

    public String makeSignatureDelete(String time, String channelId) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String space = " ";					// 공백
        String newLine = "\n";  				// 줄바꿈
        String method = "DELETE";  				// HTTP 메서드
        String url = "/api/v2/channels/"+ channelId;	// 도메인을 제외한 "/" 아래 전체 url (쿼리스트링 포함)
        String timestamp = time;		// 현재 타임스탬프 (epoch, millisecond)

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);

        return encodeBase64String;
    }
}
