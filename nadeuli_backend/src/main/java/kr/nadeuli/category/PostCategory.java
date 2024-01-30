package kr.nadeuli.category;

public enum PostCategory {

    NOTICE(0L),
    SMALL_TALK(1L),
    PROMOTION(2L),
    STREAMING(3L),
    ALBUM(4L);

    PostCategory(Long postCategory) {
        this.postCategory = postCategory;
    }

    private final Long postCategory;

    private Long getPostCategory() {
        return postCategory;
    }

    public static PostCategory fromPostCategory(Long postCategory) {
        for (PostCategory category : PostCategory.values()) {
            if (category.getPostCategory() == postCategory) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid PostCategory : " + postCategory);
    }

}

