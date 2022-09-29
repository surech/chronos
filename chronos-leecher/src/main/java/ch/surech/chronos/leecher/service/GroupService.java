package ch.surech.chronos.leecher.service;

import ch.surech.chronos.analyser.persistence.model.GroupMembers;
import com.microsoft.graph.models.DirectoryObject;
import com.microsoft.graph.models.Group;
import com.microsoft.graph.models.User;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.DirectoryObjectCollectionWithReferencesPage;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.GroupCollectionPage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupService.class);

    @Autowired
    private GraphService graphService;

    public List<Group> searchGroupByDisplayName(String displayName){
        GraphServiceClient graphClient = graphService.getGraphClient();

        final List<Option> options = new LinkedList<Option>();
        options.add(new QueryOption("$filter", "displayName eq '" + displayName + "'"));

        GroupCollectionPage groups = graphClient
            .groups()
            .buildRequest(options)
            .get();

        return groups.getCurrentPage();
    }

    public GroupMembers getMembersInGroup(Group group){
        GraphServiceClient graphClient = graphService.getGraphClient();
        DirectoryObjectCollectionWithReferencesPage groupCollectionPage = graphClient.groups(group.id).members().buildRequest().get();

        List<DirectoryObject> members = collectMembers(groupCollectionPage);

        GroupMembers groupMembers = new GroupMembers();
        for (DirectoryObject member : members) {
            if(member instanceof User){
                groupMembers.getUsers().add((User) member);
            } else if(member instanceof Group){
                groupMembers.getGroups().add((Group) member);
            } else {
                LOGGER.warn("Known member-type: " + member);
            }
        }
        return groupMembers;
    }

    public List<User> getAllUsersInGroup(Group group){
        return getAllUsersInGroup(group, new HashSet<>());
    }
    public List<User> getAllUsersInGroup(Group group, Set<String> blacklistIds){
        List<User> result = new ArrayList<>();

        GroupMembers membersInGroup = getMembersInGroup(group);
        result.addAll(membersInGroup.getUsers());

        for (Group subGroup : membersInGroup.getGroups()) {
            // Check Blacklist
            if(blacklistIds.contains(subGroup.id)){
                continue;
            }

            // Add current group to blacklist for avoiding circular dependencies
            blacklistIds.add(subGroup.id);
            result.addAll(getAllUsersInGroup(subGroup, blacklistIds));
        }

        return result;
    }

    private List<DirectoryObject> collectMembers(DirectoryObjectCollectionWithReferencesPage groupCollectionPage) {
        List<DirectoryObject> result = new ArrayList<>();
        collectMembers(groupCollectionPage, result);
        return result;
    }

    private void collectMembers(DirectoryObjectCollectionWithReferencesPage page, List<DirectoryObject> result) {
        // When no members are delivered, we can't do much here...
        if(page == null || page.getCurrentPage() == null || page.getCurrentPage().isEmpty()){
            return;
        }

        // Add members to the result
        result.addAll(page.getCurrentPage());

        if(page.getNextPage() != null){
            // Add next page
            collectMembers(page.getNextPage().buildRequest().get(), result);
        }
    }
}
