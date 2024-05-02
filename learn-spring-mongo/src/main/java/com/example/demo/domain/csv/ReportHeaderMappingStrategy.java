package com.example.demo.domain.csv;

import com.opencsv.bean.BeanField;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.commons.lang3.StringUtils;

public class ReportHeaderMappingStrategy<T> extends ColumnPositionMappingStrategy<T> {

    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
        final int numColumns = getFieldMap().values().size();
        super.generateHeader(bean);
        String[] headers = new String[numColumns];
        for (int i = 0; i < numColumns; i++) {
            BeanField<T, Integer> beanField = findField(i);
            String columnHeaderName = extractHeaderName(beanField);
            headers[i] = columnHeaderName;
        }
        return headers;
    }

    private String extractHeaderName(BeanField<T, Integer> beanField) {
        if (beanField == null || beanField.getField() == null
            || beanField.getField().getAnnotation(CsvBindByName.class) == null) {
            return StringUtils.EMPTY;
        }
        return beanField.getField().getAnnotation(CsvBindByName.class).column();
    }
}
