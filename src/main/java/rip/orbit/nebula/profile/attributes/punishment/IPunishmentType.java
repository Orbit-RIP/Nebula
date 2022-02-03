package rip.orbit.nebula.profile.attributes.punishment;

public interface IPunishmentType {

    String getReadable();
    String getExecutedContext();
    @Deprecated String getPardonedContext();

}
