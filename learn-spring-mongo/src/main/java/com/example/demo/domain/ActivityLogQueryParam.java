package com.example.demo.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.domain.Sort.Direction;

@Data
public class ActivityLogQueryParam {

    private LocalDateTime dateTimeFrom;
    private LocalDateTime dateTimeTo;
    private String serviceType;
    private String branchCode;
    private String channel;
    private String idType;
    private String idNo;
    private List<String> activityType;
    private String activityStatus;
    private Integer rmidEc;
    private String userActivity; // userActivity = serviceType + "_" + activityStatus
    private PaginationAndSort paginationAndSort;

    public PaginationAndSort createPaginationAndSort() {
        this.paginationAndSort = new PaginationAndSort();
        return this.paginationAndSort;
    }

    @Getter
    public static class PaginationAndSort {

        private boolean paged = false;
        private int pageNumber = 0;
        private int pageSize = 0;
        private boolean sorted = false;
        private String sortField;
        private Direction sortDirection;

        /**
         * Set pagination.
         *
         * @param pageNumber must be over zero.
         * @param pageSize   must be over zero.
         * @return this
         */
        public PaginationAndSort setPage(int pageNumber, int pageSize) {
            assert pageNumber > 0;
            assert pageSize > 0;
            this.paged = true;
            this.pageNumber = pageNumber - 1; // Convert to zero-based index.
            this.pageSize = pageSize;
            return this;
        }

        public PaginationAndSort setSortDesc(String fieldName) {
            setSort(fieldName, Direction.DESC);
            return this;
        }

        public PaginationAndSort setSortAcs(String fieldName) {
            setSort(fieldName, Direction.ASC);
            return this;
        }

        private void setSort(String fieldName, Direction direction) {
            this.sorted = true;
            this.sortField = fieldName;
            this.sortDirection = direction;
        }
    }
}
