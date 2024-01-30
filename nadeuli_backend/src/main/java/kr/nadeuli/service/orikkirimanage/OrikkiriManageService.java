package kr.nadeuli.service.orikkirimanage;

import kr.nadeuli.dto.*;

import java.util.List;

public interface OrikkiriManageService {


    List<OrikkiriDTO> allOrikkiri() throws Exception;

    OrikkiriDTO addOrikkiri(OrikkiriDTO orikkiriDTO) throws Exception;

    void updateOrikkiri(OrikkiriDTO orikkiriDTO) throws Exception;

    OrikkiriDTO getOrikkiri(long orikkiriId) throws Exception;

    void deleteOrikkiri(long orikkiriId) throws Exception;

    AnsQuestionDTO addAnsQuestion(AnsQuestionDTO ansQuestionDTO) throws Exception;

    void updateAnsQuestion(AnsQuestionDTO ansQuestionDTO) throws Exception;

    AnsQuestionDTO getAnsQuestion(long ansQuestionId) throws Exception;

    List<AnsQuestionDTO> getAnsQuestionList(long orikkiriId, SearchDTO searchDTO) throws Exception;

    public void deleteAnsQuestion(long ansQuestionId) throws Exception;

    public void addAns(AnsQuestionDTO ansQuestionDTO) throws Exception;

    public OriScheMemChatFavDTO getOriScheMemChatFavDTO(Long oriScheMemChatFavId) throws Exception;

    public void deleteSignUp(long oriScheMemChatFavId) throws Exception;
}
