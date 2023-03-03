package ut.com.sensorsdata.jiraplugin;

import org.junit.Test;
import com.sensorsdata.jiraplugin.api.MyPluginComponent;
import com.sensorsdata.jiraplugin.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}