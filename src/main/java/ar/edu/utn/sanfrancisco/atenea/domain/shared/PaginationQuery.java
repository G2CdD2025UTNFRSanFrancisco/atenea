package ar.edu.utn.sanfrancisco.atenea.domain.shared;

public record PaginationQuery(
        int page,
        int size,
        SortDirection direction,
        String sortBy
) {

    private static final int MAX_PAGE_SIZE = 100;

    public PaginationQuery {
        if (page < 0) {
            throw new BusinessException("Page cannot be less than 0", "request.invalid", 400) {};
        }
        if (size <= 0) {
            throw new BusinessException("Size must be greater than 0", "request.invalid", 400) {};
        }
        if (size > MAX_PAGE_SIZE) {
            throw new BusinessException("Page size too large", "request.invalid", 400) {};
        }
    }

    public int offset() {
        return Math.multiplyExact(page, size);
    }

    public int limit() {
        return size;
    }

    public boolean isFirstPage() {
        return page == 0;
    }

    public PaginationQuery next() {
        return new PaginationQuery(page + 1, size, direction, sortBy);
    }

    public PaginationQuery previous() {
        if (isFirstPage()) return this;
        return new PaginationQuery(page - 1, size, direction, sortBy);
    }

    public static PaginationQuery of(int page, int size) {
        return new PaginationQuery(page, size, SortDirection.ASC, "id");
    }
}
