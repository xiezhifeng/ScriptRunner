package jira.postfunctions.console_tests

import com.atlassian.jira.bc.issue.IssueService
import com.atlassian.jira.component.ComponentAccessor

def selectListField = ComponentAccessor.customFieldManager.getCustomFieldObject(10400l)
def optionToSelect = ComponentAccessor.getOptionsManager().getOptions(selectListField.getRelevantConfig(issue))
        .find {it.value == "CEM"}
def currentValue = issue.getCustomFieldValue(selectListField)

if (optionToSelect != null && currentValue == null) {

    def issueService = ComponentAccessor.getIssueService()
    def issueInputParameters = issueService.newIssueInputParameters()

    def currentUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
    issueInputParameters.addCustomFieldValue(selectListField.idAsLong, optionToSelect.optionId.toString())
            .setSkipScreenCheck(true)
    IssueService.UpdateValidationResult validationResult = issueService.validateUpdate(currentUser, issue.id, issueInputParameters)
    if (validationResult.valid) issueService.update(currentUser, validationResult)
}