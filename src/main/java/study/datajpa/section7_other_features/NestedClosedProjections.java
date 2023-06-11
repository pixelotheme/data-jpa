package study.datajpa.section7_other_features;

public interface NestedClosedProjections {

    String getUsername();
    TeamInfo getTeam();

    interface TeamInfo{
        String getName();
    }
}
