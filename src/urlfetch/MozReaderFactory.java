package urlfetch;

public class MozReaderFactory {

    public MozReader instanceOf() {
        return new MozCsvReader();
    }
}
