package enums;


import strategies.*;

public enum Role {
    ADMIN(new AdminRoleStrategy()),
    TECHNICIAN(new TechnicianRoleStrategy()),
    ENGINEER(new EngineerRoleStrategy()),
    COORDINATOR(new CoordinatorRoleStrategy());

    private final RoleStrategy strategy;

    Role(RoleStrategy strategy) {
        this.strategy = strategy;
    }

    public RoleStrategy getStrategy() {
        return strategy;
    }

    public boolean createUser() {
        return strategy.createUser();
    }

    public boolean readUser() {
        return strategy.readUser();
    }

    public boolean updateUser() {
        return strategy.updateUser();
    }

    public boolean deleteUser() {
        return strategy.deleteUser();
    }

    public boolean createItem() {
        return strategy.createItem();
    }


    public boolean deleteItem() {
        return strategy.deleteItem();
    }

    public boolean existItem() {
        return strategy.existItem();
    }

    public boolean createProject(){
        return strategy.createProject();
    }

    public boolean deleteProject() {
        return strategy.deleteProject();
    }

    public boolean existProject() {
        return strategy.existProject();
    }

    public boolean createPlace(){
        return strategy.createPlace();
    }

    public boolean deletePlace(){
        return strategy.deletePlace();
    }

    public boolean existPlace(){
        return strategy.existPlace();
    }




}
