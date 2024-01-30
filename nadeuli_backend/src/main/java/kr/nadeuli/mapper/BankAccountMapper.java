package kr.nadeuli.mapper;

import kr.nadeuli.dto.BankAccountDTO;
import kr.nadeuli.entity.BankAccount;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(builder = @Builder(disableBuilder = true),componentModel = "spring", uses = MemberMapper.class)
public interface BankAccountMapper {

  BankAccount bankAccountDTOToBankAccount(BankAccountDTO bankAccountDTO);

  BankAccountDTO bankAccountToBankAccountDTO(BankAccount bankAccount);

}