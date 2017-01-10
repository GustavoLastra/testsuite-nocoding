package com.xceptance.xlt.common.util.action.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;

import com.xceptance.xlt.api.util.XltLogger;
import com.xceptance.xlt.common.util.action.data.URLActionData;
import com.xceptance.xlt.common.util.action.data.URLActionDataStore;
import com.xceptance.xlt.common.util.action.data.URLActionDataValidation;

/**
 * Handles a {@link URLActionDataValidation} item. <br>
 * <ul>
 * <li>Reads the selectionMode & selectionContent of {@link URLActionDataStore}.
 * <li>Selects the described elements in {@link URLActionDataExecutableResult}.
 * <li>Validates the result of the selection with the described validationMode & validationContent in
 * {@link URLActionDataStore}.
 * <li>For this use {@link #validate(URLActionDataValidation, URLActionDataExecutableResult) validate()}.
 * </ul>
 * 
 * @author matthias mitterreiter
 */
public class URLActionDataValidationResponseHandler
{
    public URLActionDataValidationResponseHandler()
    {
        XltLogger.runTimeLogger.debug("Creating new Instance");
    }

    /**
     * Does the following:
     * <ul>
     * <li>fetches the elements from the {@link URLActionDataExecutableResult result} that suit the described criteria
     * in {@link URLActionDataValidation validation}.
     * <li>validates the found elements.
     * </ul>
     * 
     * @param validation
     *            : the description of the validation process.
     * @param result
     *            : the response to validate in form of a {@link URLActionDataExecutableResult}.
     * @throws IllegalArgumentException
     *             if the validation was wrong or failed.
     */
    public void validate(final URLActionDataValidation validation, final URLActionDataExecutableResult result, final URLActionData action)
    {
        XltLogger.runTimeLogger.debug("Validating: \"" + validation.getName() + "\"");
        try
        {
            handleValidation(validation, result, action);
        }
        catch (final Exception e)
        {
            throw new IllegalArgumentException("Failed to validate Response : \"" + validation.getName() + "\" for action \"" + action.getName() + "\": " + e.getMessage(), e);
        }
    }

    private void handleValidation(final URLActionDataValidation validation, final URLActionDataExecutableResult result, final URLActionData action)
    {
        final List<String> resultSelection = selectFromResult(validation, result, action);
        validateContent(resultSelection, validation, action);

    }

    private List<String> selectFromResult(final URLActionDataValidation validation, final URLActionDataExecutableResult result, final URLActionData action)
    {
        final String selectionMode = validation.getSelectionMode();

        List<String> resultSelection = new ArrayList<String>();

        switch (selectionMode)
        {
            case URLActionDataValidation.XPATH:
                resultSelection = handleXPathValidationItem(validation, result);
                break;
            case URLActionDataValidation.REGEXP:
                resultSelection = handleRegExValidationItem(validation, result);
                break;
            case URLActionDataValidation.HEADER:
                resultSelection = handleHeaderValidationItem(validation, result);
                break;
            case URLActionDataValidation.COOKIE:
                resultSelection = handleCookieValidationItem(validation, result);
                break;
            default:
                throw new IllegalArgumentException("SelectionMode: \"" + validation.getSelectionMode() + "\" is not supported!");
        }
        return resultSelection;
    }

    private void validateContent(final List<String> resultSelection, final URLActionDataValidation validation, final URLActionData action)
    {
        final String validationMode = validation.getValidationMode();
        switch (validationMode)
        {
            case URLActionDataValidation.EXISTS:
                validateExists(resultSelection, validation, action);
                break;
            case URLActionDataValidation.COUNT:
                validateCount(resultSelection, validation, action);
                break;
            case URLActionDataValidation.MATCHES:
                validateMatches(resultSelection, validation, action);
                break;
            case URLActionDataValidation.TEXT:
                validateText(resultSelection, validation, action);
                break;
            default:
                throw new IllegalArgumentException("ValidationMode: \"" + validation.getValidationMode() + "\" is not supported!");
        }
    }

    private void validateExists(final List<String> resultSelection, final URLActionDataValidation validation, final URLActionData action)
    {
        XltLogger.runTimeLogger.debug("Validating \"" + validation.getName() + "\": EXISTANCE");

        Assert.assertFalse(getNotFoundFailMessage(validation, action), resultSelection.isEmpty());
    }

    private void validateCount(final List<String> resultSelection, final URLActionDataValidation validation, final URLActionData action)
    {

        validateExists(resultSelection, validation, action);

        final int expectedLength = Integer.valueOf(validation.getValidationContent());
        final int actualLength = resultSelection.size();

        XltLogger.runTimeLogger.debug("Validating  \"" + validation.getName() + "\": COUNT:" + expectedLength + " = \"" + actualLength +
                                      "\"");

        Assert.assertEquals(getFailMessage(validation, action), expectedLength, actualLength);

    }

    private void validateMatches(final List<String> resultSelection, final URLActionDataValidation validation, final URLActionData action)
    {
        validateExists(resultSelection, validation, action);

        final String matcherString = resultSelection.get(0);
        final String patternString = validation.getValidationContent();

        XltLogger.runTimeLogger.debug("Validating  \"" + validation.getName() + "\": MATCHES: \"" + matcherString + "\" matches \"" +
                                      patternString + "\"");

        final Pattern pattern = Pattern.compile(patternString);
        final Matcher matcher = pattern.matcher(matcherString);

        Assert.assertTrue(getFailMessage(validation, action), matcher.find());

    }

    private void validateText(final List<String> resultSelection, final URLActionDataValidation validation, final URLActionData action)
    {
        validateExists(resultSelection, validation, action);

        final String expectedText = validation.getValidationContent();
        final String actualText = resultSelection.get(0);

        XltLogger.runTimeLogger.debug("Validating  \"" + validation.getName() + "\": TEXT: " + "'" + expectedText + "'" + " = " + "'" +
                                      actualText + "'");

        Assert.assertEquals(getFailMessage(validation, action), expectedText, actualText);
    }

    private List<String> handleCookieValidationItem(final URLActionDataValidation validation, final URLActionDataExecutableResult result)
    {
        return result.getCookieAsStringByName(validation.getSelectionContent());
    }

    private List<String> handleXPathValidationItem(final URLActionDataValidation validation, final URLActionDataExecutableResult result)
    {
        return result.getByXPath(validation.getSelectionContent());

    }

    private List<String> handleRegExValidationItem(final URLActionDataValidation validation, final URLActionDataExecutableResult result)
    {
        return result.getByRegEx(validation.getSelectionContent());

    }

    private List<String> handleHeaderValidationItem(final URLActionDataValidation validation, final URLActionDataExecutableResult result)
    {
        return result.getHeaderByName(validation.getSelectionContent());

    }

    private String getNotFoundFailMessage(final URLActionDataValidation validation, final URLActionData action)
    {
        final String message = MessageFormat.format("Validation \"{0}\" failed for action \"{1}\", because for {2} = \"{3}\" no Elements were found! ",
                                                    validation.getName(), action.getName(), validation.getSelectionMode(), validation.getSelectionContent());
        return message;
    }

    private String getFailMessage(final URLActionDataValidation validation, final URLActionData action)
    {
        final String message = MessageFormat.format("Validation \"{0}\" failed for action \"{1}\", Mode: \"{2}\":", validation.getName(), action.getName(),
                                                    validation.getValidationMode());
        return message;
    }

}
