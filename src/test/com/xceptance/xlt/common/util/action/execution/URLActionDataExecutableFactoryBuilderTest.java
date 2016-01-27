package test.com.xceptance.xlt.common.util.action.execution;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.xceptance.xlt.api.util.XltProperties;
import com.xceptance.xlt.common.util.NoCodingPropAdmin;
import com.xceptance.xlt.common.util.action.execution.HtmlPageActionFactory;
import com.xceptance.xlt.common.util.action.execution.LightWeightPageActionFactory;
import com.xceptance.xlt.common.util.action.execution.URLActionDataExecutionableFactory;
import com.xceptance.xlt.common.util.action.execution.URLActionDataExecutionbleFactoryBuilder;

public class URLActionDataExecutableFactoryBuilderTest
{
    private static NoCodingPropAdmin propAdmin;

    @BeforeClass
    public static void setup()
    {
        propAdmin = new NoCodingPropAdmin(XltProperties.getInstance(), "", "");
    }

    @SuppressWarnings("static-access")
    @Test
    public void testCorrectConstructorCreation()
    {
        @SuppressWarnings("unused")
        URLActionDataExecutionbleFactoryBuilder factoryBuilder;
        factoryBuilder = new URLActionDataExecutionbleFactoryBuilder(this.propAdmin, URLActionDataExecutionbleFactoryBuilder.MODE_DOM);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongConstructorParameterInterpreter()
    {
        @SuppressWarnings("unused")
        final URLActionDataExecutionbleFactoryBuilder factory = new URLActionDataExecutionbleFactoryBuilder(
                                                                                                            null,
                                                                                                            URLActionDataExecutionbleFactoryBuilder.MODE_DOM);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongConstructorParameterMode()
    {
        @SuppressWarnings(
            {
                "unused", "static-access"
            })
        final URLActionDataExecutionbleFactoryBuilder factory = new URLActionDataExecutionbleFactoryBuilder(this.propAdmin, "someMode");
    }

    @Test
    public void testCorrectDomBuildingResult()
    {
        @SuppressWarnings("static-access")
        final URLActionDataExecutionbleFactoryBuilder factory = new URLActionDataExecutionbleFactoryBuilder(
                                                                                                            this.propAdmin,
                                                                                                            URLActionDataExecutionbleFactoryBuilder.MODE_DOM);
        final URLActionDataExecutionableFactory actionFactory = factory.buildFactory();
        Assert.assertTrue(actionFactory instanceof HtmlPageActionFactory);
    }

    @Test
    public void testCorrectLightBuildingResult()
    {
        @SuppressWarnings("static-access")
        final URLActionDataExecutionbleFactoryBuilder factory = new URLActionDataExecutionbleFactoryBuilder(
                                                                                                            this.propAdmin,
                                                                                                            URLActionDataExecutionbleFactoryBuilder.MODE_LIGHT);
        final URLActionDataExecutionableFactory actionFactory = factory.buildFactory();
        Assert.assertTrue(actionFactory instanceof LightWeightPageActionFactory);
    }

}
