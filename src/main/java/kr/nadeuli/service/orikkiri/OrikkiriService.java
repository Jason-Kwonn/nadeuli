package kr.nadeuli.service.orikkiri;

import kr.nadeuli.dto.*;

import java.util.List;

public interface OrikkiriService {

    OriScheMemChatFavDTO addOrikkrirSignUp(OriScheMemChatFavDTO oriScheMemChatFavDTO) throws Exception;

    List<AnsQuestionDTO> getOrikkiriSignUpList(long orikkiriId, SearchDTO searchDTO) throws Exception;

    List<OriScheMemChatFavDTO> getMyOrikkiriList(String tag, SearchDTO searchDTO) throws Exception;

    List<OriScheMemChatFavDTO> getOrikkiriMemberList(long orikkiriId, SearchDTO searchDTO) throws Exception;

    MemberDTO getAnsMember(OriScheMemChatFavDTO oriScheMemChatFavDTO)throws Exception;

    void deleteOrikkiriMember(String tag, long oriScheMemChatFavId) throws Exception;

    void addOrikkiriScheduleMember(OriScheMemChatFavDTO oriScheMemChatFavDTO) throws Exception;

    void addOrikkiriMember(OriScheMemChatFavDTO oriScheMemChatFavDTO) throws Exception;

    List<OriScheMemChatFavDTO> getOrikkiriScheduleMemberList(long orikkiriScheduleId, SearchDTO searchDTO) throws Exception;

    void addOrikkiriSchedule(OrikkiriScheduleDTO orikkiriScheduleDTO) throws Exception;

    List<OrikkiriScheduleDTO> getOrikkiriScheduleList(long orikkiriId, SearchDTO searchDTO) throws Exception;

    OrikkiriScheduleDTO getOrikkiriSchedule(long orikkiriScheduleId) throws Exception;

    void updateOrikkiriSchedule(OrikkiriScheduleDTO orikkiriScheduleDTO) throws Exception;

    void deleteOrikkiriSchedule(long orikkiriScheduleId) throws Exception;
}
