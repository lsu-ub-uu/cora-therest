package se.uu.ub.cora.therest.data;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.Action;

public class RestDataRecordLinkTest {
    private RestDataRecordLink recordLink;

    @BeforeMethod
    public void setUp(){
        recordLink = RestDataRecordLink.withNameInData("aNameInData");
        RestDataAtomic linkedRecordType = RestDataAtomic.withNameInDataAndValue("linkedRecordType", "aLinkedRecordType");
        recordLink.addChild(linkedRecordType);

        RestDataAtomic linkedRecordId = RestDataAtomic.withNameInDataAndValue("linkedRecordId", "aLinkedRecordId");
        recordLink.addChild(linkedRecordId);
    }

    @Test
    public void testInit(){
        assertEquals(recordLink.getChildren().size(), 2);
        assertNotNull(recordLink.getFirstChildWithNameInData("linkedRecordType"));
        assertTrue(recordLink.containsChildWithNameInData("linkedRecordId"));
    }

    @Test
    public void testWithActionLinks() {
        ActionLink actionLink = ActionLink.withAction(Action.READ);
        recordLink.addActionLink("read", actionLink);
        assertEquals(recordLink.getActionLink("read"), actionLink);
        assertEquals(recordLink.getActionLinks().get("read"), actionLink);
        assertNull(recordLink.getActionLink("notAnAction"));
    }

    @Test
    public void testWithRepeatId() {
        recordLink.setRepeatId("x2");
        assertEquals(recordLink.getRepeatId(), "x2");
    }
}
