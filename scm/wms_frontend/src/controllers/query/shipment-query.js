import { useMutation, useQuery } from "@tanstack/react-query";
import { toast } from "react-toastify";
import { queryClient } from "../../App";
import axiosSendRequest from "../axiosSendRequest";
import { endPoint } from "../endpoint";
import { queryKey } from "./querykey";

export const useCreateShipment = (params) => {
  return useMutation({
    mutationFn: async (data) => {
      const res = await axiosSendRequest(
        "post",
        endPoint.createShipment,
        params,
        data
      );
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    onSuccess: (res, variables, context) => {
      toast.success("Tạo đợt giao hàng thành công!");
      queryClient.invalidateQueries([queryKey.shipment.shipment_list]);
    },
    onError: () => {
      toast.error("Lỗi khi tạo kế hoạch, vui lòng kiểm tra lại");
    },
    // befor mutation function actually triggers.
    onMutate: (variables) => {},
  });
};
export const useGetShipmentList = (params) => {
  return useQuery({
    queryKey: [queryKey.shipment.shipment_list, params],
    queryFn: async () => {
      const res = await axiosSendRequest("get", endPoint.getShipments);
      if (res.data && res.code === 1) {
        return res.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {},
  });
};
