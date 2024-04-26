package com.example.demo.domain;

import java.time.LocalDateTime;
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
    private String activityType;
    private String activityStatus;
    private Integer rmidEc;
    private PaginationAndSort paginationAndSort;

    public PaginationAndSort createPaginationAndSort() {
        this.paginationAndSort = new PaginationAndSort();
        return this.paginationAndSort;
    }

    @Getter
    public static class PaginationAndSort {

        /**
         * 0 will be ignored.
         */
        private int pageNumber = 0;
        /**
         * 0 will be ignored.
         */
        private int pageSize = 0;
        private boolean sort = false;
        private String sortField;
        private Direction sortDirection;

        public PaginationAndSort setPage(int pageNumber, int pageSize) {
            this.pageNumber = pageNumber;
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
            this.sort = true;
            this.sortField = fieldName;
            this.sortDirection = direction;
        }
    }
}
