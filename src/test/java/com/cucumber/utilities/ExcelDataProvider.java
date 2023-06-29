//package com.cucumber.utilities;
//
////import com.nextgen.annotation.ExcelDetails;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Method;
//
//public class ExcelDataProvider {
//
//	private ThreadLocal<String> classExcelName = new ThreadLocal<>();
//	private ThreadLocal<String> classSheetName = new ThreadLocal<>();
//	private ThreadLocal<String> methodExcelName = new ThreadLocal<>();
//	private ThreadLocal<String> methodSheetName = new ThreadLocal<>();
//	private ThreadLocal<Class<?>> testClass = new ThreadLocal<>();
//
//	public ExcelDataProvider(Class<?> testClass) {
//		this.testClass.set(testClass);
//	}
//
//	private void getExcelDetailsFromClass() {
//		if (testClass.get().isAnnotationPresent(ExcelDetails.class)) {
//			Annotation annotation = testClass.get().getAnnotation(ExcelDetails.class);
//			ExcelDetails excelDetails = (ExcelDetails) annotation;
//			if (excelDetails.excelName().isEmpty()) {
//				classExcelName.set(testClass.get().getSimpleName());
//			} else {
//				classExcelName.set(excelDetails.excelName());
//			}
//			classSheetName.set(excelDetails.sheetName());
//		}
//	}
//
//	private void getExcelDetailsFromMethod(Method method) {
//		if (method.isAnnotationPresent(ExcelDetails.class)) {
//			Annotation annotation = method.getAnnotation(ExcelDetails.class);
//			ExcelDetails excelDetails = (ExcelDetails) annotation;
//			if (excelDetails.excelName().isEmpty()) {
//				methodExcelName.set(method.getName());
//			} else {
//				methodExcelName.set(excelDetails.excelName());
//			}
//			methodSheetName.set(excelDetails.sheetName());
//		}
//	}
//
//	public Object[][] data(Method method) {
//		getExcelDetailsFromMethod(method);
//		if (methodExcelName.get() != null && methodSheetName.get() != null) {
//			return ReadExcel.getData(methodExcelName.get(), methodSheetName.get());
//		}
//		getExcelDetailsFromClass();
//		return ReadExcel.getData(classExcelName.get(), classSheetName.get());
//	}
//
//}
