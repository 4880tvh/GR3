import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import { Box } from "@mui/material";
import { Action } from "components/action/Action";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import DraggableDeleteDialog from "components/dialog/DraggableDialogs";
import CustomDrawer from "components/drawer/CustomDrawer";
import CustomModal from "components/modal/CustomModal";
import HeaderModal from "components/modal/HeaderModal";
import CustomToolBar from "components/toolbar/CustomToolBar";
import {
  useDeleteContractType,
  useGetContractType,
} from "controllers/query/category-query";
import { useState } from "react";
import { useToggle, useWindowSize } from "react-use";
import { AppColors } from "shared/AppColors";
import { contractTypeCols } from "../LocalConstant";
import CreateContractTypeForm from "./components/CreateContractTypeForm";
import UpdateContractType from "./components/UpdateContractType";
function ContractTypeScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    pageSize: 10,
  });
  const { height } = useWindowSize();
  const [isAdd, setIsAdd] = useToggle(false);
  const [isOpenDrawer, setOpenDrawer] = useToggle(false);
  const [isRemove, setIsRemove] = useToggle(false);
  const [itemSelected, setItemSelected] = useState(null);

  const { isLoading, data } = useGetContractType(params);
  const deleteContractQuery = useDeleteContractType({
    code: itemSelected?.code,
  });
  let actions = [
    {
      title: "Thêm",
      callback: (pre) => {
        setIsAdd((pre) => !pre);
      },
      icon: <AddIcon />,
      describe: "Thêm bản ghi mới",
      disabled: false,
    },
  ];
  const extraActions = [
    {
      title: "Sửa",
      callback: (item) => {
        setOpenDrawer((pre) => !pre);
        setItemSelected(item);
      },
      icon: <EditIcon />,
      color: AppColors.secondary,
    },
    {
      title: "Xóa",
      callback: (item) => {
        setIsRemove();
        setItemSelected(item);
      },
      icon: <DeleteIcon />,
      color: AppColors.error,
    },
  ];
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box>
        <CustomToolBar actions={actions} />
      </Box>
      <CustomDataGrid
        params={params}
        setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
        isLoading={isLoading}
        totalItem={data?.totalElements}
        handlePaginationModelChange={(props) => {
          setParams({
            page: props?.page + 1,
            pageSize: props?.pageSize,
          });
        }}
        columns={[
          ...contractTypeCols,
          {
            field: "action",
            headerName: "Hành động",
            headerAlign: "center",
            align: "center",
            sortable: false,
            flex: 1,
            type: "actions",
            getActions: (params) => [
              ...extraActions.map((extraAction, index) => (
                <Action
                  item={params.row}
                  key={index}
                  extraAction={extraAction}
                  onActionCall={extraAction.callback}
                  disabled={false}
                />
              )),
            ],
          },
        ]}
        rows={data ? data?.content : []}
      />
      <CustomModal
        open={isAdd}
        toggle={setIsAdd}
        size="sm"
        title="Thêm loại hợp đồng"
      >
        <CreateContractTypeForm setIsAdd={setIsAdd} />
      </CustomModal>
      <CustomDrawer open={isOpenDrawer} onClose={setOpenDrawer}>
        <HeaderModal onClose={setOpenDrawer} title="Sửa thông tin hợp đồng" />
        <UpdateContractType
          currContract={itemSelected}
          setOpenDrawer={setOpenDrawer}
        />
      </CustomDrawer>
      <DraggableDeleteDialog
        // disable={isLoadingRemove}
        open={isRemove && itemSelected}
        handleOpen={setIsRemove}
        callback={async (flag) => {
          if (flag) {
            await deleteContractQuery.mutateAsync();
          }
          setIsRemove(false);
        }}
      />
    </Box>
  );
}

const SCR_ID = "SCR_SCM_CONTRACT_TYPE";
export default withScreenSecurity(ContractTypeScreen, SCR_ID, true);
