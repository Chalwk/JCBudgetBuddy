cmake_minimum_required(VERSION 3.24)

project(JCBudgetBuddy VERSION 1.0.0 LANGUAGES CXX)

set(PRODUCT_NAME  "${PROJECT_NAME}")
set(PRODUCT_PUBLISHER "Chalwk")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)
set(CMAKE_AUTOUIC ON)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

find_package(Qt6 REQUIRED COMPONENTS Widgets PrintSupport)

add_executable(${PROJECT_NAME} WIN32
    resources/resources.qrc
    src/data/DataManager.cpp
    src/data/DataManager.h
    src/delegates/ButtonDelegate.cpp
    src/delegates/ButtonDelegate.h
    src/dialogs/ItemDialogs.cpp
    src/dialogs/ItemDialogs.h
    src/main.cpp
    src/MainWindow.cpp
    src/MainWindow.h
    src/models/Bill.cpp
    src/models/Bill.h
    src/models/IncomeStream.cpp
    src/models/IncomeStream.h
    src/models/Invoice.cpp
    src/models/Invoice.h
    src/models/Payment.cpp
    src/models/Payment.h
    src/models/PlanItem.cpp
    src/models/PlanItem.h
    src/models/UserData.cpp
    src/models/UserData.h
    src/utils/FinanceUtils.cpp
    src/utils/FinanceUtils.h
    src/widgets/BaseTableWidget.cpp
    src/widgets/BaseTableWidget.h
    src/widgets/DashboardWidget.cpp
    src/widgets/DashboardWidget.h
    src/widgets/IncomeWidget.cpp
    src/widgets/IncomeWidget.h
    src/widgets/InvoicesWidget.cpp
    src/widgets/InvoicesWidget.h
    src/widgets/MonthlyBillsWidget.cpp
    src/widgets/MonthlyBillsWidget.h
    src/widgets/PlanWidget.cpp
    src/widgets/PlanWidget.h
    src/widgets/SavingsGoalsWidget.cpp
    src/widgets/SavingsGoalsWidget.h
    src/widgets/WeeklyBillsWidget.cpp
    src/widgets/WeeklyBillsWidget.h
)

target_include_directories(${PROJECT_NAME} PRIVATE src ${CMAKE_CURRENT_BINARY_DIR}/src)

target_link_libraries(${PROJECT_NAME} PRIVATE Qt6::Widgets Qt6::PrintSupport)

if (MSVC)
    target_compile_options(${PROJECT_NAME} PRIVATE /W4 /permissive-)
else()
    target_compile_options(${PROJECT_NAME} PRIVATE -Wall -Wextra -Wpedantic)
endif()

install(TARGETS ${PROJECT_NAME} RUNTIME DESTINATION .)

set(CPACK_GENERATOR "NSIS")
set(CPACK_PACKAGE_NAME ${PROJECT_NAME})
set(CPACK_PACKAGE_VENDOR ${PROJECT_NAME})
set(CPACK_PACKAGE_VERSION ${PROJECT_VERSION})
set(CPACK_NSIS_DISPLAY_NAME ${PROJECT_NAME})
set(CPACK_NSIS_PACKAGE_NAME ${PROJECT_NAME})

include(CPack)