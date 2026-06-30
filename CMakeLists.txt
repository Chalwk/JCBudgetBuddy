cmake_minimum_required(VERSION 3.24)

project(JCBudgetBuddy VERSION 1.0.0 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)
set(CMAKE_AUTOUIC ON)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

find_package(Qt6 REQUIRED COMPONENTS Widgets)

set(SOURCES
    src/main.cpp
    src/MainWindow.cpp
    src/utils/FinanceUtils.cpp
    src/data/DataManager.cpp
    src/delegates/ButtonDelegate.cpp
    src/models/Payment.cpp
    src/models/Invoice.cpp
    src/models/IncomeStream.cpp
    src/models/Bill.cpp
    src/models/PlanItem.cpp
    src/models/UserData.cpp
    src/widgets/BaseTableWidget.cpp
    src/widgets/DashboardWidget.cpp
    src/widgets/IncomeWidget.cpp
    src/widgets/WeeklyBillsWidget.cpp
    src/widgets/MonthlyBillsWidget.cpp
    src/widgets/InvoicesWidget.cpp
    src/widgets/PlanWidget.cpp
    src/widgets/SavingsGoalsWidget.cpp
    src/dialogs/ItemDialogs.cpp
    resources/resources.qrc
)

set(HEADERS
    include/MainWindow.h
    include/utils/FinanceUtils.h
    include/data/DataManager.h
    include/delegates/ButtonDelegate.h
    include/models/Payment.h
    include/models/Invoice.h
    include/models/IncomeStream.h
    include/models/Bill.h
    include/models/PlanItem.h
    include/models/UserData.h
    include/widgets/BaseTableWidget.h
    include/widgets/DashboardWidget.h
    include/widgets/IncomeWidget.h
    include/widgets/WeeklyBillsWidget.h
    include/widgets/MonthlyBillsWidget.h
    include/widgets/InvoicesWidget.h
    include/widgets/PlanWidget.h
    include/widgets/SavingsGoalsWidget.h
    include/dialogs/ItemDialogs.h
)

add_executable(JCBudgetBuddy WIN32 ${SOURCES} ${HEADERS})

target_include_directories(JCBudgetBuddy PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/include
    ${CMAKE_CURRENT_SOURCE_DIR}/src
)

target_link_libraries(JCBudgetBuddy PRIVATE Qt6::Widgets)

if (MSVC)
    target_compile_options(JCBudgetBuddy PRIVATE /W4 /permissive-)
else()
    target_compile_options(JCBudgetBuddy PRIVATE -Wall -Wextra -Wpedantic)
endif()

install(TARGETS JCBudgetBuddy RUNTIME DESTINATION .)

set(CPACK_GENERATOR "NSIS")
set(CPACK_PACKAGE_NAME "JCBudgetBuddy")
set(CPACK_PACKAGE_VENDOR "JCBudgetBuddy")
set(CPACK_PACKAGE_VERSION ${PROJECT_VERSION})
set(CPACK_NSIS_DISPLAY_NAME "JCBudgetBuddy")
set(CPACK_NSIS_PACKAGE_NAME "JCBudgetBuddy")
include(CPack)