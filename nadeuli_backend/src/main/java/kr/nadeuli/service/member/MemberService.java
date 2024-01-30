package kr.nadeuli.service.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import kr.nadeuli.dto.AddressDTO;
import kr.nadeuli.dto.BlockDTO;
import kr.nadeuli.dto.GpsDTO;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.NadeuliDeliveryDTO;
import kr.nadeuli.dto.NadeuliPayHistoryDTO;
import kr.nadeuli.dto.OriScheMemChatFavDTO;
import kr.nadeuli.dto.PortOneAccountDTO;
import kr.nadeuli.dto.ReportDTO;
import kr.nadeuli.dto.SearchDTO;


public interface MemberService {

  public String addTag() throws Exception;

  public MemberDTO updateMember(MemberDTO memberDTO) throws Exception;

  public MemberDTO updateTo(MemberDTO memberDTO) throws Exception;

  public MemberDTO getMember(String tag) throws Exception;

  public MemberDTO getOtherMember(String tag) throws Exception;

  public List<MemberDTO> getMemberList(SearchDTO searchDTO) throws Exception;

  public AddressDTO addDongNe(String tag, GpsDTO gpsDTO) throws Exception;

  public void addBlockMember(BlockDTO blockDTO, String tag) throws Exception;

  public void deleteBlockMember(String tag) throws Exception;

  public void handleMemberActivate(String tag) throws Exception;

  public void handleNadeuliDelivery(String tag) throws Exception;

  public void addFavorite(String tag, Long productId) throws Exception;

  public void deleteFavorite(String tag, Long productId) throws Exception;

  public List<OriScheMemChatFavDTO> getFavoriteList(String tag, SearchDTO searchDTO)
      throws Exception;

  public void addReport(ReportDTO reportDTO) throws Exception;

  public String getAffinityToolTip() throws Exception;

  public boolean handleNadeuliPayBalance(String tag, NadeuliPayHistoryDTO nadeuliPayHistoryDTO,
      NadeuliDeliveryDTO nadeuliDeliveryDTO, Long beforeDeposit) throws Exception;

  public void updateAffinity(String tag) throws Exception;

  public boolean findAccount(String email) throws Exception;

  public boolean updateCellphone(MemberDTO memberDTO) throws Exception;

  String getPortOneToken() throws JsonProcessingException;

  String getPortOneAccountName(String accessToken, String account, String code)
      throws JsonProcessingException;

  boolean checkPortOneAccountName(String name, String account, String code)
      throws JsonProcessingException;

  public void addBankAccount(PortOneAccountDTO portOneAccountDTO) throws Exception;

  public MemberDTO deleteBankAccount(MemberDTO memberDTO) throws Exception;
}
