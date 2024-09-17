package poc

import com.example.pdfboxpoc.service.PdfProcessService
import spock.lang.Shared
import spock.lang.Specification

class PdfPoc extends Specification {

    @Shared
    PdfProcessService pdfProcessService = new PdfProcessService()

    def "Split"() {
        when:
        pdfProcessService.split()

        then:
        1 == 1
    }

    def "Merge"() {
        when:
        pdfProcessService.merge()

        then:
        1 == 1
    }
}
