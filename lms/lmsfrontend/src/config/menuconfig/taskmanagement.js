export const taskmanagement = {
  id: "MENU_BACKLOG",
  path: "",
  isPublic: false,
  icon: "AssignmentOutlinedIcon",
  text: "Theo dõi dự án",
  child: [
    {
      id: "MENU_BACKLOG_VIEW_LIST_PROJECT",
      path: "/taskmanagement/project/list",
      isPublic: false,
      icon: "StarBorder",
      text: "Danh sách dự án",
      child: [],
    },
    {
      id: "MENU_BACKLOG_CREATE_TASK_PROJECT",
      path: "/taskmanagement/project/tasks/create",
      isPublic: false,
      icon: "StarBorder",
      text: "Tạo mới nhiệm vụ",
      child: [],
    },
    {
      id: "MENU_BACKLOG_VIEW_MY_TASK",
      path: "/taskmanagement/tasks/members/assigned",
      isPublic: false,
      icon: "StarBorder",
      text: "Danh sách nhiệm vụ được giao",
      child: [],
    },
    {
      id: "MENU_BACKLOG_COMMON_MANAGER",
      path: "/taskmanagement/common-manager",
      isPublic: false,
      icon: "StarBorder",
      text: "Quản lý chung",
      child: [],
    },
  ],
};