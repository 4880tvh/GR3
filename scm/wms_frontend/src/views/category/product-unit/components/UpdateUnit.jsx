import { Box, Button, Stack } from "@mui/material";
import { useUpdateProductUnit } from "../../../../controllers/query/category-query";

const { FormProvider, useForm, Controller } = require("react-hook-form");
const { default: CustomInput } = require("components/input/CustomInput");

const UpdateProductUnit = ({ currCate, setOpenDrawer }) => {
  const methods = useForm({
    mode: "onChange",
    defaultValues: {
      name: currCate?.name,
    },
    // resolver: yupResolver(productCategorySchema),
  });
  const {
    handleSubmit,
    formState: { errors },
    reset,
    control,
  } = methods;

  const updateProductUnit = useUpdateProductUnit({
    id: currCate?.id,
  });

  const onSubmit = async (data) => {
    let params = {
      name: data?.name.trim(),
    };
    await updateProductUnit.mutateAsync(params);
    setOpenDrawer((pre) => !pre);
    reset();
  };
  return (
    <FormProvider {...methods}>
      <Box sx={{ marginTop: 5 }}></Box>
      <Stack direction="row" justifyContent={"space-around"} spacing={5}>
        <Controller
          key={"name"}
          control={control}
          name={"name"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"text"}
              onChange={onChange}
              label={"Tên danh mục"}
              isFullWidth={true}
              error={!!errors["name"]}
              message={errors["name"]?.message}
            />
          )}
        />
      </Stack>
      <Stack
        direction="row"
        justifyContent={"flex-end"}
        spacing={2}
        sx={{ marginBottom: 2 }}
      >
        <Button
          onClick={handleSubmit(onSubmit)}
          variant="contained"
          style={{ color: "white" }}
        >
          Submit
        </Button>
      </Stack>
    </FormProvider>
  );
};
export default UpdateProductUnit;