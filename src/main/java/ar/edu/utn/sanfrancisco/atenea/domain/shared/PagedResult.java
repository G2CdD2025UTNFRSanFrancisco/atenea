package ar.edu.utn.sanfrancisco.atenea.domain.shared;

import java.util.List;

public record PagedResult<T>(
        List<T> items,
        long totalItems,
        int currentPage,
        int pageSize,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {

    public static <T> PagedResult<T> of(
            List<T> items,
            long totalItems,
            PaginationQuery query
    ) {
        int totalPages = (int) Math.ceil((double) totalItems / query.limit());

        boolean hasNext = query.page() + 1 < totalPages;
        boolean hasPrevious = query.page() > 0;

        return new PagedResult<>(
                items,
                totalItems,
                query.page(),
                query.limit(),
                totalPages,
                hasNext,
                hasPrevious
        );
    }
}
