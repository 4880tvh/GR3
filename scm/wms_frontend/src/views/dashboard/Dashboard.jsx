import { Box, Stack, Typography } from "@mui/material";
import { blue, deepOrange, indigo, orange } from "@mui/material/colors";
import AreaChart from "../../components/chart/AreaChart";
import BarChart from "../../components/chart/BarChart";
import ColumnChart from "../../components/chart/ColumnChart";
import LineChart from "../../components/chart/LineChart";
import MapChart from "../../components/chart/MapChart";
import PieChart from "../../components/chart/PieChart";
import PieChart2 from "../../components/chart/PieChart2";
import {
  useGetImportedProduct,
  useGetNewCustomerMonthly,
  useGetNewFacilityMonthly,
  useGetProductCategoryRate,
  useGetPurchaseOrderQuarterly,
  useGetTopCustomerBuying,
  useGetTripCustomerPerProvince,
} from "../../controllers/query/dashboard-query";
import { getRandomIntInclusive } from "../../layout/notification/Notification";
const mappingData = [
  // ["vn-3655"],
  ["vn-qn", "Quảng Ninh"],
  ["vn-kh", "Khánh Hòa"],
  ["vn-tg", "Tiền Giang"],
  ["vn-bv", "Bà Rịa Vũng Tàu"],
  ["vn-bu", "Bình Thuận"],
  ["vn-hc", "Hồ Chí Minh"],
  ["vn-br", "Bến Tre"],
  ["vn-st", "Sóc Trăng"],
  ["vn-pt", "Phú Thọ"],
  ["vn-yb", "Yên Bái"],
  ["vn-hd", "Hải Dương"],
  ["vn-bn", "Bắc Ninh"],
  ["vn-317", "Hưng Yên"],
  ["vn-nb", "Ninh Bình"],
  ["vn-hm", "Hà Nam"],
  ["vn-ho", "Hòa Bình"],
  ["vn-vc", "Vĩnh Phúc"],
  ["vn-318", "Hà Nội"],
  ["vn-bg", "Bắc Giang"],
  ["vn-tb", "Thái Bình"],
  ["vn-ld", "Lâm Đồng"],
  ["vn-bp", "Bình Phước"],
  ["vn-py", "Phú Yên"],
  ["vn-bd", "Bình Định"],
  ["vn-724", "Gia Lai"],
  ["vn-qg", "Quảng Ngãi"],
  ["vn-331", "Đồng Nai"],
  ["vn-dt", "Đồng Tháp"],
  ["vn-la", "Long An"],
  ["vn-3623", "Hải Phòng"],
  ["vn-337", "Hậu Giang"],
  ["vn-bl", "Bạc Liêu"],
  ["vn-vl", "Vĩnh Long"],
  ["vn-tn", "Tây Ninh"],
  ["vn-ty", "Thái Nguyên"],
  ["vn-li", "Lai Châu"],
  ["vn-311", "Sơn La"],
  ["vn-hg", "Hà Giang"],
  ["vn-nd", "Nam Định"],
  ["vn-328", "Hà Tĩnh"],
  ["vn-na", "Nghệ An"],
  ["vn-qb", "Quảng Bình"],
  ["vn-723", "Đắk Lắk"],
  ["vn-nt", "Ninh Thuận"],
  ["vn-6365", "Đắk Nông"],
  ["vn-299", "Kon Tum"],
  ["vn-300", "Quảng Nam"],
  ["vn-qt", "Quảng Trị"],
  ["vn-tt", "Huế"],
  ["vn-da", "Đà Nẵng"],
  ["vn-ag", "An Giang"],
  ["vn-cm", "Cà Mau"],
  ["vn-tv", "Trà Vinh"],
  ["vn-cb", "Cao Bằng"],
  ["vn-kg", "Kiên giang"],
  ["vn-lo", "Lào Cai"],
  ["vn-db", "Điện Biên"],
  ["vn-ls", "Lạng Sơn"],
  ["vn-th", "Thanh Hóa"],
  ["vn-307", "Bắc Kạn"],
  ["vn-tq", "Tuyên Quang"],
  ["vn-bi", "Bình Dương"],
  ["vn-333", "Cần Thơ"],
];
const DashBoard = () => {
  const { isLoading: loadNewFacility, data: facility } =
    useGetNewFacilityMonthly({
      year: 2023,
    });
  const { isLoading: loadNewCustomer, data: customer } =
    useGetNewCustomerMonthly({
      year: 2023,
    });
  const { isLoading: loadImportedProduct, data: importedProducts } =
    useGetImportedProduct({
      month: 5,
      year: 2023,
    });
  const { isLoading: loadPurchaseOrderQuarter, data: quaterPurchaseOrder } =
    useGetPurchaseOrderQuarterly({
      quarter: 2,
      year: 2023,
    });
  const { isLoading: loadTopFiveCustomer, data: topFiveCustomer } =
    useGetTopCustomerBuying({
      month: 5,
      year: 2023,
    });
  const { isLoading: loadProductCategory, data: productCategory } =
    useGetProductCategoryRate();
  const { isLoading: loadTripCustomers, data: tripCustomers } =
    useGetTripCustomerPerProvince({
      month: 5,
      year: 2023,
    });
  return (
    <Box>
      <Stack direction="row">
        <Box sx={{ p: 2, width: "50%" }}>
          <Stack
            direction="row"
            spacing={2}
            sx={{ alignItems: "center", mb: 2 }}
          >
            <Typography sx={{ textTransform: "uppercase", fontWeight: 600 }}>
              Số lượng nhà phân phối mở mới
            </Typography>
          </Stack>
          <BarChart
            loading={loadNewFacility}
            categories={facility ? facility?.map((el) => el) : []}
            series={[
              {
                name: "Number of facilities",
                data: facility?.map((el) => el),
                color: blue[500],
              },
            ]}
          />
        </Box>
        <Box sx={{ p: 2, width: "50%" }}>
          <Stack
            direction="row"
            spacing={2}
            sx={{ alignItems: "center", mb: 2 }}
          >
            <Typography sx={{ textTransform: "uppercase", fontWeight: 600 }}>
              Số lượng khách hàng mở mới
            </Typography>
          </Stack>
          <BarChart
            loading={loadNewCustomer}
            categories={customer ? customer?.map((el) => el) : []}
            series={[
              {
                name: "Number of customers",
                data: customer?.map((el) => el),
                color: blue[500],
              },
            ]}
          />
        </Box>
      </Stack>
      <Stack direction="row">
        <Box sx={{ p: 2, width: "50%" }}>
          <Stack
            direction="column"
            spacing={2}
            sx={{ alignItems: "center", mb: 2 }}
          >
            <Typography
              sx={{ textTransform: "uppercase", fontWeight: 600, mb: 1 }}
            >
              {"KPI trong tháng"}
              {/* {"Top khách hàng mua nhiều trong tháng"} */}
            </Typography>
            <ColumnChart
              legend
              grouping={false}
              loading={loadTopFiveCustomer}
              series={
                [
                  {
                    name: "Chỉ tiêu sellout",
                    data: topFiveCustomer?.map(() =>
                      getRandomIntInclusive(100, 1000)
                    ),
                    pointPlacement: 0.3,
                    color: orange[100],
                  },
                  {
                    name: "Doanh số sellout",
                    data: topFiveCustomer?.map(() =>
                      getRandomIntInclusive(100, 1000)
                    ),
                    pointPlacement: 0.3,
                    color: deepOrange[400],
                  },
                  {
                    name: "Chỉ tiêu sellin",
                    data: topFiveCustomer?.map(() =>
                      getRandomIntInclusive(100, 1000)
                    ),
                    color: blue[100],
                  },
                  {
                    name: "Doanh số sellin",
                    data: topFiveCustomer?.map(() =>
                      getRandomIntInclusive(100, 1000)
                    ),
                    color: indigo[400],
                  },
                ]
                // topFiveCustomer ? topFiveCustomer?.map((cus) => cus[1]) : []
              }
              categories={
                ["GT", "ADG", "ACT", "MT", "OT"]
                // topFiveCustomer
                //   ? topFiveCustomer?.map((cus) => cus[0]?.name)
                //   : []
              }
            />
          </Stack>
        </Box>
        <Box sx={{ p: 2, width: "50%" }}>
          <Stack
            direction="column"
            spacing={2}
            sx={{ alignItems: "center", mb: 2 }}
          >
            <Typography
              sx={{ textTransform: "uppercase", fontWeight: 600, mb: 1 }}
            >
              {"Tỉ trọng nhập kho"}
            </Typography>
            <PieChart loading={loadImportedProduct} series={importedProducts} />
          </Stack>
        </Box>
      </Stack>
      <Stack direction="row">
        <Box sx={{ p: 2, width: "50%" }}>
          <Stack
            direction="column"
            spacing={2}
            sx={{ alignItems: "center", mb: 2 }}
          >
            <Typography
              sx={{ textTransform: "uppercase", fontWeight: 600, mb: 1 }}
            >
              {"Đơn hàng trong quý"}
            </Typography>
            <LineChart
              loading={loadPurchaseOrderQuarter}
              categories={
                quaterPurchaseOrder ? Object.keys(quaterPurchaseOrder) : []
              }
              series={[
                {
                  name: "Tạo mới",
                  data: quaterPurchaseOrder
                    ? Object.values(quaterPurchaseOrder)?.map((orders) => {
                        return orders[0]["CREATED"];
                      })
                    : null,
                },
                {
                  name: "Đã phê duyệt nhập kho",
                  data: quaterPurchaseOrder
                    ? Object.values(quaterPurchaseOrder)?.map((orders) => {
                        return orders[1]["ACCEPTED"];
                      })
                    : [],
                },
                {
                  name: "Đang giao",
                  data: quaterPurchaseOrder
                    ? Object.values(quaterPurchaseOrder)?.map((orders) => {
                        return orders[2]["DELIVERING"];
                      })
                    : [],
                },
                {
                  name: "Đã giao",
                  data: quaterPurchaseOrder
                    ? Object.values(quaterPurchaseOrder)?.map((orders) => {
                        return orders[3]["DELIVERED"];
                      })
                    : [],
                },
              ]}
            />
          </Stack>
        </Box>
        <Box sx={{ p: 2, width: "50%" }}>
          <Stack
            direction="column"
            spacing={2}
            sx={{ alignItems: "center", mb: 2 }}
          >
            <Typography
              sx={{ textTransform: "uppercase", fontWeight: 600, mb: 1 }}
            >
              {""}
            </Typography>
            <AreaChart />
          </Stack>
        </Box>
      </Stack>
      <Stack direction="row">
        <Box sx={{ p: 2, width: "100%" }}>
          <Typography
            sx={{ textTransform: "uppercase", fontWeight: 600, mb: 1 }}
          >
            {"Tỷ trọng danh mục sản phẩm"}
          </Typography>
          <PieChart2
            series={[
              {
                name: "Tỉ trọng",
                colorByPoint: true,
                data: productCategory?.map((el) => {
                  return {
                    name: el[0]?.name,
                    y: el[1],
                    drilldown: el[0]?.name,
                  };
                }),
              },
            ]}
          />
        </Box>
      </Stack>
      <Box sx={{ p: 2, width: "100%" }}>
        <Stack
          direction="column"
          spacing={2}
          sx={{ alignItems: "center", mb: 2 }}
        >
          <Typography
            sx={{ textTransform: "uppercase", fontWeight: 600, mb: 1 }}
          >
            {"Chuyến giao hàng toàn quốc"}
          </Typography>
        </Stack>
        <MapChart
          loading={false}
          data={tripCustomers?.map((res) => {
            let mapping = mappingData?.map((a, idx) => {
              return a[1].toLowerCase() === res[0].toLowerCase() ? idx : -1;
            });
            let index = mapping.findIndex((a) => a !== -1);
            return [mappingData[index][0], res[1]];
          })}
        />
      </Box>
    </Box>
  );
};
export default DashBoard;
