package epc.therest.record;

import epc.metadataformat.data.DataElement;
import epc.metadataformat.validator.DataValidator;
import epc.metadataformat.validator.ValidationAnswer;

/**
 *
 *
 * @author <a href="mailto:madeleine.kennback@ub.uu.se">Madeleine Kennbäck</a>
 * @version $Revision$, $Date$, $Author$
 */
public class DataValidatorReturnNullPointer implements DataValidator {
    @Override
    public ValidationAnswer validateData(String metadataId, DataElement dataGroup) {
        throw new NullPointerException();
    }
}
