package jira.postfunctions.console_tests

import com.atlassian.jira.bc.issue.IssueService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.MutableIssue

def issueManager = ComponentAccessor.issueManager
def issue = issueManager.getIssueObject("TESSR-1") as MutableIssue

def sourceSLField = ComponentAccessor.customFieldManager.getCustomFieldObject(10705l)
def sourceValue = sourceSLField.getValue(issue)
def destSLField = ComponentAccessor.customFieldManager.getCustomFieldObject(10803l)
def destValue = destSLField.getValue(issue)

if (sourceValue && !destValue) {
    def optionToSelect = ComponentAccessor.optionsManager.getOptions(destSLField.getRelevantConfig(issue))
            .find {it.value == sourceValue.toString()}

    if (optionToSelect) {
        def issueService = ComponentAccessor.issueService
        def issueInputParameters = issueService.newIssueInputParameters()
        def currentUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
        issueInputParameters.addCustomFieldValue(destSLField.idAsLong, optionToSelect.optionId.toString())
                .setSkipScreenCheck(true)
        IssueService.UpdateValidationResult validationResult = issueService.validateUpdate(currentUser, issue.id, issueInputParameters)
        if (validationResult.valid) issueService.update(currentUser, validationResult)
    }
}