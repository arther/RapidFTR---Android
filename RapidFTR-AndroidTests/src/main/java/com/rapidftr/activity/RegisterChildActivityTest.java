package com.rapidftr.activity;


import com.rapidftr.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

public class RegisterChildActivityTest extends BaseActivityIntegrationTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginPage.login();
        solo.waitForText("Login Successful");
        waitUntilTextDisappears("Login Successful");
        childPage.navigateToRegisterPage();
    }

    @Override
    public void tearDown() throws Exception {
        solo.goBackToActivity("MainActivity");
        loginPage.logout();
        super.tearDown();
    }
//     df
    public void estFormSectionsDisplayed() {
        List<String> actualSections = childPage.getDropDownFormSections();
        List<String> expectedSections = new ArrayList<String>(asList(new String[]{"Basic Identity", "Family details", "Care Arrangements", "Separation History", "Protection Concerns",
                "Childs Wishes", "Other Interviews", "Other Tracing Info", "Interview Details", "Automation Form"}));
        assertEquals(actualSections, expectedSections);
    }

    //df
    public void estFieldsDisplayed() {
        childPage.selectFormSection("Automation Form");
        List expectedFields = asList("Automation TextField", "Automation TextArea", "Automation CheckBoxes", "Automation Select",
                "Automation Radio", "Automation Number", "Automation Date");
        childPage.verifyFields(expectedFields, true);
    }

    public void testFieldsHidden() {
        childPage.selectFormSection("Automation Form");
        List hiddenField = asList("Hidden TextField");
        childPage.verifyFields(hiddenField, false);
    }
//   df
    public void testRegisterChild() {
        childPage.selectFormSection("Automation Form");
        List automationFormData = Arrays.asList("Automation TextField value", "Automation TextArea value", "Check 1", "Select 1", "Radio 3", "1", "20", "10", "2012");
        childPage.enterAutomationFormDetails(automationFormData);
        childPage.save();
        childPage.verifyRegisterChildDetail(automationFormData, "Automation Form");
    }
//    df
    public void testRegisterAndSyncChild() {
        childPage.selectFormSection("Automation Form");
        List automationFormData = asList("Automation TextField value", "Automation TextArea value", "Check 3", "Select 1", "Radio 3", "1", "20", "10", "2012");
        childPage.enterAutomationFormDetails(automationFormData);
        childPage.save();
        childPage.verifyRegisterChildDetail(automationFormData, "Automation Form");
    }

    public void testEditChild() {
        String name = "Test Edit Child";
        childPage.selectFormSection("Automation Form");
        childPage.registerChild();
        childPage.selectEditChild();
        childPage.selectFormSection("Basic Identity");
        childPage.enterChildName(name);
        childPage.save();
        solo.waitForText("Saved Record Successfully");
        assertTrue(isEditedTextPresent(name));
    }


    public void estNavigatingAwayFromRegisterPromptUserToSave(){
        childPage.enterChildName("MsgPromptText");
        viewAllChildrenPage.navigateToViewAllTab();
        isTextPresent("Choose an action");


    }
    public void xtestIfNavigatingAwayFromRegisterPromptsUserToSave() {
        String name = "Test";
        childPage.enterChildName(name);
        solo.clickOnText("View All");
        assertTrue(solo.waitForText("Save"));
    }

    public void xtestIfNavigatingAwayFromRegisterPageDoesnotPromptIfChildInvalid() throws InterruptedException {
        solo.clickOnText("View All");
        assertFalse(solo.waitForText("Choose an option"));
    }

    public void xtestIfDiscardTakesYouToTheNextActivity() throws InterruptedException {
        childPage.enterChildName("Test");
        solo.clickOnText("Search");
        solo.clickOnText("Discard");
        assertTrue(solo.waitForText("Go"));
    }

    public void xtestIfUserIsPromptedToSaveWhenLeavingEditPage() {
        String name = "Test Edit Child";
        childPage.selectFormSection("Automation Form");
        childPage.registerChild();
        childPage.selectEditChild();
        childPage.selectFormSection("Basic Identity");
        childPage.enterChildName(name);
        solo.clickOnText("Search");
        assertTrue(solo.waitForText("Go"));
    }

    public void xtestIfUserIsPromptedToSaveWhenBackButtonIsPressed() {
        childPage.enterChildName("Name");
        solo.goBack();
        assertTrue(solo.waitForText("Save"));
        solo.clickOnText("Cancel");
    }

    public void xtestIfCancelButtonLeavesTakesYouNowhere() {
        childPage.enterChildName("Name");
        solo.goBack();
        solo.clickOnText("Cancel");
        assertTrue(solo.waitForText("Name"));
    }

    public void xtestIfUserIsPromptedToSaveWhenLogoutIsPressed() {
        childPage.enterChildName("Name");
        solo.clickOnMenuItem(solo.getString(R.string.log_out));
        assertTrue(solo.waitForText("Save"));
    }
}
