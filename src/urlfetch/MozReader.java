package urlfetch;

import java.util.Collection;

/**
 * 
 * @author wvanderhoef
 */
public interface MozReader {

    public Collection<MozData> fromFile(String fileName);
}
