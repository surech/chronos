package ch.surech.chronos.leecher.model;

import com.microsoft.graph.models.Group;
import com.microsoft.graph.models.User;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class GroupMembers {

    private final List<User> users = new ArrayList<>();
    private final List<Group> groups = new ArrayList<>();
}
