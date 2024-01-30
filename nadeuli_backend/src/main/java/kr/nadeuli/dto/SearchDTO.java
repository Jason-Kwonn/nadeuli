package kr.nadeuli.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchDTO {
    private String searchKeyword;
    private int currentPage;
    private int pageSize;
    private boolean isSold;
    private boolean isBuyer;
    private boolean isWriter;
    private boolean isPost;
    private boolean isProduct;
}
