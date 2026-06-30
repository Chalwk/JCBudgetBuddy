// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#include "dialogs/ItemDialogs.h"

#include <QFormLayout>
#include <QDialogButtonBox>
#include <QMessageBox>
#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QHeaderView>
#include <QStyle>
#include <QApplication>
#include <QStandardItem>

static QDoubleSpinBox* makeMoneySpin() {
    auto* spin = new QDoubleSpinBox;
    spin->setMaximum(1e9);
    spin->setDecimals(2);
    spin->setPrefix("$");
    return spin;
}

IncomeDialog::IncomeDialog(QWidget* parent, const IncomeStream* existing)
    : QDialog(parent) {
    setWindowTitle(existing ? "Edit Income Stream" : "Add Income Stream");
    resize(520, 420);

    m_nameEdit = new QLineEdit;
    m_amountSpin = makeMoneySpin();
    m_frequencyCombo = new QComboBox;
    m_frequencyCombo->addItems({ "Weekly", "Fortnightly", "Monthly", "Yearly", "One-off" });
    m_startDateEdit = new QDateEdit(QDate::currentDate());
    m_startDateEdit->setCalendarPopup(true);
    m_useEndDateCheck = new QCheckBox("Use end date");
    m_endDateEdit = new QDateEdit(QDate::currentDate().addMonths(1));
    m_endDateEdit->setCalendarPopup(true);
    m_activeCheck = new QCheckBox("Active");
    m_activeCheck->setChecked(true);
    m_notesEdit = new QTextEdit;

    connect(m_useEndDateCheck, &QCheckBox::toggled, m_endDateEdit, &QWidget::setEnabled);
    m_endDateEdit->setEnabled(false);

    auto* form = new QFormLayout;
    form->addRow("Name", m_nameEdit);
    form->addRow("Amount", m_amountSpin);
    form->addRow("Frequency", m_frequencyCombo);
    form->addRow("Start Date", m_startDateEdit);
    form->addRow("", m_useEndDateCheck);
    form->addRow("End Date", m_endDateEdit);
    form->addRow("", m_activeCheck);
    form->addRow("Notes", m_notesEdit);

    auto* buttons = new QDialogButtonBox(QDialogButtonBox::Ok | QDialogButtonBox::Cancel);
    connect(buttons, &QDialogButtonBox::accepted, this, &IncomeDialog::accept);
    connect(buttons, &QDialogButtonBox::rejected, this, &IncomeDialog::reject);

    auto* layout = new QVBoxLayout(this);
    layout->addLayout(form);
    layout->addWidget(buttons);

    if (existing) {
        m_nameEdit->setText(existing->name);
        m_amountSpin->setValue(existing->amount);
        m_frequencyCombo->setCurrentText(incomeFrequencyToString(existing->frequency));
        if (existing->startDate.isValid()) m_startDateEdit->setDate(existing->startDate);
        if (existing->endDate && existing->endDate->isValid()) {
            m_useEndDateCheck->setChecked(true);
            m_endDateEdit->setDate(*existing->endDate);
        }
        m_activeCheck->setChecked(existing->active);
        m_notesEdit->setPlainText(existing->notes);
    }
}

IncomeStream IncomeDialog::resultData() const {
    IncomeStream stream;
    stream.name = m_nameEdit->text().trimmed();
    stream.amount = m_amountSpin->value();
    stream.frequency = incomeFrequencyFromString(m_frequencyCombo->currentText());
    stream.startDate = m_startDateEdit->date();
    if (m_useEndDateCheck->isChecked()) {
        stream.endDate = m_endDateEdit->date();
    }
    stream.active = m_activeCheck->isChecked();
    stream.notes = m_notesEdit->toPlainText().trimmed();
    return stream;
}

void IncomeDialog::accept() {
    if (m_nameEdit->text().trimmed().isEmpty()) {
        QMessageBox::warning(this, "Validation", "Please enter an income name.");
        return;
    }
    if (m_amountSpin->value() < 0.0) {
        QMessageBox::warning(this, "Validation", "Amount cannot be negative.");
        return;
    }
    QDialog::accept();
}

BillDialog::BillDialog(bool monthlyMode, QWidget* parent, const Bill* existing)
    : QDialog(parent), m_monthlyMode(monthlyMode) {
    setWindowTitle(existing ? "Edit Bill" : "Add Bill");
    resize(480, 400);

    m_nameEdit = new QLineEdit;
    m_amountSpin = makeMoneySpin();
    m_frequencyCombo = new QComboBox;
    m_frequencyCombo->addItems({ "Weekly", "Bi-Weekly", "Monthly" });
    m_paymentDayEdit = new QLineEdit;
    m_paymentMethodCombo = new QComboBox;
    m_paymentMethodCombo->addItems({ "manual", "automatic" });
    m_spreadWeeklyCheck = new QCheckBox("Include in weekly expenses (spread monthly amount across 4 weeks)");
    m_notesEdit = new QTextEdit;

    if (!m_monthlyMode) {
        m_paymentMethodCombo->setCurrentText("manual");
        m_paymentMethodCombo->setEnabled(false);
        m_spreadWeeklyCheck->setVisible(false);
    }

    auto* form = new QFormLayout;
    form->addRow("Name", m_nameEdit);
    form->addRow("Amount", m_amountSpin);
    form->addRow("Frequency", m_frequencyCombo);
    form->addRow("Payment Day", m_paymentDayEdit);
    if (m_monthlyMode) {
        form->addRow("Payment Method", m_paymentMethodCombo);
        form->addRow("", m_spreadWeeklyCheck);
    } else {
        form->addRow("Payment Method", new QLabel("manual"));
    }
    form->addRow("Notes", m_notesEdit);

    auto* buttons = new QDialogButtonBox(QDialogButtonBox::Ok | QDialogButtonBox::Cancel);
    connect(buttons, &QDialogButtonBox::accepted, this, &BillDialog::accept);
    connect(buttons, &QDialogButtonBox::rejected, this, &BillDialog::reject);

    auto* layout = new QVBoxLayout(this);
    layout->addLayout(form);
    layout->addWidget(buttons);

    if (existing) {
        m_nameEdit->setText(existing->name);
        m_amountSpin->setValue(existing->amount);
        m_frequencyCombo->setCurrentText(billFrequencyToString(existing->frequency));
        m_paymentDayEdit->setText(existing->paymentDay);
        m_paymentMethodCombo->setCurrentText(paymentMethodToString(existing->paymentMethod));
        if (m_monthlyMode) {
            m_spreadWeeklyCheck->setChecked(existing->spreadWeekly);
        }
        m_notesEdit->setPlainText(existing->notes);
    } else {
        if (m_monthlyMode) {
            bool isAuto = (m_paymentMethodCombo->currentText() == "automatic");
            m_spreadWeeklyCheck->setChecked(isAuto);
        }
    }
}

Bill BillDialog::resultData() const {
    Bill bill;
    bill.name = m_nameEdit->text().trimmed();
    bill.amount = m_amountSpin->value();
    bill.frequency = billFrequencyFromString(m_frequencyCombo->currentText());
    bill.paymentDay = m_paymentDayEdit->text().trimmed();
    bill.paymentMethod = m_monthlyMode ? paymentMethodFromString(m_paymentMethodCombo->currentText()) : BillPaymentMethod::Manual;
    bill.notes = m_notesEdit->toPlainText().trimmed();
    if (m_monthlyMode) {
        bill.spreadWeekly = m_spreadWeeklyCheck->isChecked();
    } else {
        bill.spreadWeekly = false;
    }
    return bill;
}

void BillDialog::accept() {
    if (m_nameEdit->text().trimmed().isEmpty()) {
        QMessageBox::warning(this, "Validation", "Please enter a bill name.");
        return;
    }
    if (m_amountSpin->value() < 0.0) {
        QMessageBox::warning(this, "Validation", "Amount cannot be negative.");
        return;
    }
    if (m_paymentDayEdit->text().trimmed().isEmpty()) {
        QMessageBox::warning(this, "Validation", "Please enter a payment day.");
        return;
    }
    QDialog::accept();
}

InvoiceDialog::InvoiceDialog(QWidget* parent, const Invoice* existing)
    : QDialog(parent) {
    setWindowTitle(existing ? "Edit Invoice" : "Add Invoice");
    resize(380, 180);

    m_numberEdit = new QLineEdit;
    m_totalSpin = makeMoneySpin();

    auto* form = new QFormLayout;
    form->addRow("Invoice Number", m_numberEdit);
    form->addRow("Total Amount", m_totalSpin);

    auto* buttons = new QDialogButtonBox(QDialogButtonBox::Ok | QDialogButtonBox::Cancel);
    connect(buttons, &QDialogButtonBox::accepted, this, &InvoiceDialog::accept);
    connect(buttons, &QDialogButtonBox::rejected, this, &InvoiceDialog::reject);

    auto* layout = new QVBoxLayout(this);
    layout->addLayout(form);
    layout->addWidget(buttons);

    if (existing) {
        m_numberEdit->setText(existing->number);
        m_totalSpin->setValue(existing->total);
    }
}

Invoice InvoiceDialog::resultData() const {
    Invoice invoice;
    invoice.number = m_numberEdit->text().trimmed();
    invoice.total = m_totalSpin->value();
    return invoice;
}

void InvoiceDialog::accept() {
    if (m_numberEdit->text().trimmed().isEmpty()) {
        QMessageBox::warning(this, "Validation", "Please enter an invoice number.");
        return;
    }
    QDialog::accept();
}

PaymentDialog::PaymentDialog(QWidget* parent, const Payment* existing)
    : QDialog(parent) {
    setWindowTitle(existing ? "Edit Payment" : "Add Payment");
    resize(320, 160);

    m_dateEdit = new QDateEdit(QDate::currentDate());
    m_dateEdit->setCalendarPopup(true);
    m_amountSpin = makeMoneySpin();

    auto* form = new QFormLayout;
    form->addRow("Date", m_dateEdit);
    form->addRow("Amount", m_amountSpin);

    auto* buttons = new QDialogButtonBox(QDialogButtonBox::Ok | QDialogButtonBox::Cancel);
    connect(buttons, &QDialogButtonBox::accepted, this, &PaymentDialog::accept);
    connect(buttons, &QDialogButtonBox::rejected, this, &PaymentDialog::reject);

    auto* layout = new QVBoxLayout(this);
    layout->addLayout(form);
    layout->addWidget(buttons);

    if (existing) {
        if (existing->date.isValid()) m_dateEdit->setDate(existing->date);
        m_amountSpin->setValue(existing->amount);
    }
}

Payment PaymentDialog::resultData() const {
    Payment payment;
    payment.date = m_dateEdit->date();
    payment.amount = m_amountSpin->value();
    return payment;
}

void PaymentDialog::accept() {
    if (m_amountSpin->value() < 0.0) {
        QMessageBox::warning(this, "Validation", "Amount cannot be negative.");
        return;
    }
    QDialog::accept();
}

// ==================== PlanDialog ====================

PlanDialog::PlanDialog(QWidget* parent, const PlanItem* existing)
    : QDialog(parent) {
    setWindowTitle(existing ? "Edit Plan Item" : "Add Plan Item");
    resize(520, 420);

    m_nameEdit = new QLineEdit;
    m_descEdit = new QTextEdit;
    m_totalSpin = makeMoneySpin();
    m_depositSpin = makeMoneySpin();
    m_weeklyPaymentSpin = makeMoneySpin();

    auto* form = new QFormLayout;
    form->addRow("Item Name", m_nameEdit);
    form->addRow("Description", m_descEdit);
    form->addRow("Total Cost", m_totalSpin);
    form->addRow("Deposit", m_depositSpin);
    form->addRow("Weekly Payment", m_weeklyPaymentSpin);

    auto* buttons = new QDialogButtonBox(QDialogButtonBox::Ok | QDialogButtonBox::Cancel);
    connect(buttons, &QDialogButtonBox::accepted, this, &PlanDialog::accept);
    connect(buttons, &QDialogButtonBox::rejected, this, &PlanDialog::reject);

    auto* layout = new QVBoxLayout(this);
    layout->addLayout(form);
    layout->addWidget(buttons);

    if (existing) {
        m_nameEdit->setText(existing->name);
        m_descEdit->setPlainText(existing->description);
        m_totalSpin->setValue(existing->totalCost);
        m_depositSpin->setValue(existing->deposit);
        m_weeklyPaymentSpin->setValue(existing->weeklyPayment);
    }
}

PlanItem PlanDialog::resultData() const {
    PlanItem item;
    item.name = m_nameEdit->text().trimmed();
    item.description = m_descEdit->toPlainText().trimmed();
    item.totalCost = m_totalSpin->value();
    item.deposit = m_depositSpin->value();
    item.weeklyPayment = m_weeklyPaymentSpin->value();
    return item;
}

void PlanDialog::accept() {
    if (m_nameEdit->text().trimmed().isEmpty()) {
        QMessageBox::warning(this, "Validation", "Please enter a plan item name.");
        return;
    }
    if (m_totalSpin->value() < 0.0 || m_depositSpin->value() < 0.0 || m_weeklyPaymentSpin->value() < 0.0) {
        QMessageBox::warning(this, "Validation", "Money values cannot be negative.");
        return;
    }
    if (m_depositSpin->value() > m_totalSpin->value()) {
        QMessageBox::warning(this, "Validation", "Deposit cannot exceed total cost.");
        return;
    }
    QDialog::accept();
}

PaymentsDialog::PaymentsDialog(Invoice* invoice, QWidget* parent)
    : QDialog(parent), m_invoice(invoice), m_workingCopy(invoice ? *invoice : Invoice{}) {
    setWindowTitle(QString("Payments for Invoice %1").arg(invoice ? invoice->number : ""));
    resize(560, 400);

    m_table = new QTableView;
    m_model = new QStandardItemModel(this);
    m_model->setHorizontalHeaderLabels({ "Date", "Amount" });
    m_table->setModel(m_model);
    m_table->setSelectionBehavior(QAbstractItemView::SelectRows);
    m_table->setSelectionMode(QAbstractItemView::SingleSelection);
    m_table->setEditTriggers(QAbstractItemView::NoEditTriggers);
    m_table->horizontalHeader()->setStretchLastSection(true);

    m_addButton = new QPushButton("Add");
    m_editButton = new QPushButton("Edit");
    m_deleteButton = new QPushButton("Delete");

    connect(m_addButton, &QPushButton::clicked, this, &PaymentsDialog::addPayment);
    connect(m_editButton, &QPushButton::clicked, this, &PaymentsDialog::editPayment);
    connect(m_deleteButton, &QPushButton::clicked, this, &PaymentsDialog::deletePayment);
    connect(m_table, &QTableView::doubleClicked, this, &PaymentsDialog::onDoubleClicked);

    auto* buttonRow = new QHBoxLayout;
    buttonRow->addWidget(m_addButton);
    buttonRow->addWidget(m_editButton);
    buttonRow->addWidget(m_deleteButton);
    buttonRow->addStretch();

    auto* closeButton = new QPushButton("Close");
    connect(closeButton, &QPushButton::clicked, this, &PaymentsDialog::accept);

    auto* closeRow = new QHBoxLayout;
    closeRow->addStretch();
    closeRow->addWidget(closeButton);

    auto* layout = new QVBoxLayout(this);
    layout->addWidget(m_table);
    layout->addLayout(buttonRow);
    layout->addLayout(closeRow);

    refresh();
}

void PaymentsDialog::refresh() {
    m_model->removeRows(0, m_model->rowCount());
    for (const auto& payment : m_workingCopy.payments) {
        QList<QStandardItem*> row;
        auto* dateItem = new QStandardItem(formatDate(payment.date));
        auto* amountItem = new QStandardItem(QString("$%1").arg(payment.amount, 0, 'f', 2));
        row << dateItem << amountItem;
        m_model->appendRow(row);
    }
    m_table->resizeColumnsToContents();
}

void PaymentsDialog::addPayment() {
    PaymentDialog dialog(this);
    if (dialog.exec() == QDialog::Accepted) {
        m_workingCopy.payments.append(dialog.resultData());
        refresh();
    }
}

void PaymentsDialog::editPayment() {
    const int row = m_table->currentIndex().row();
    if (row < 0 || row >= m_workingCopy.payments.size()) return;
    PaymentDialog dialog(this, &m_workingCopy.payments[row]);
    if (dialog.exec() == QDialog::Accepted) {
        m_workingCopy.payments[row] = dialog.resultData();
        refresh();
    }
}

void PaymentsDialog::deletePayment() {
    const int row = m_table->currentIndex().row();
    if (row < 0 || row >= m_workingCopy.payments.size()) return;
    if (QMessageBox::question(this, "Delete Payment", "Delete the selected payment?") == QMessageBox::Yes) {
        m_workingCopy.payments.removeAt(row);
        refresh();
    }
}

void PaymentsDialog::onDoubleClicked(const QModelIndex&) {
    editPayment();
}

void PaymentsDialog::accept() {
    if (m_invoice) {
        *m_invoice = m_workingCopy;
    }
    QDialog::accept();
}

PlanPaymentsDialog::PlanPaymentsDialog(PlanItem* plan, QWidget* parent)
    : QDialog(parent), m_plan(plan), m_workingCopy(plan ? *plan : PlanItem{}) {
    setWindowTitle(QString("Payments for Plan: %1").arg(plan ? plan->name : ""));
    resize(560, 400);

    m_table = new QTableView;
    m_model = new QStandardItemModel(this);
    m_model->setHorizontalHeaderLabels({ "Date", "Amount" });
    m_table->setModel(m_model);
    m_table->setSelectionBehavior(QAbstractItemView::SelectRows);
    m_table->setSelectionMode(QAbstractItemView::SingleSelection);
    m_table->setEditTriggers(QAbstractItemView::NoEditTriggers);
    m_table->horizontalHeader()->setStretchLastSection(true);

    m_addButton = new QPushButton("Add");
    m_editButton = new QPushButton("Edit");
    m_deleteButton = new QPushButton("Delete");

    connect(m_addButton, &QPushButton::clicked, this, &PlanPaymentsDialog::addPayment);
    connect(m_editButton, &QPushButton::clicked, this, &PlanPaymentsDialog::editPayment);
    connect(m_deleteButton, &QPushButton::clicked, this, &PlanPaymentsDialog::deletePayment);
    connect(m_table, &QTableView::doubleClicked, this, &PlanPaymentsDialog::onDoubleClicked);

    auto* buttonRow = new QHBoxLayout;
    buttonRow->addWidget(m_addButton);
    buttonRow->addWidget(m_editButton);
    buttonRow->addWidget(m_deleteButton);
    buttonRow->addStretch();

    auto* closeButton = new QPushButton("Close");
    connect(closeButton, &QPushButton::clicked, this, &PlanPaymentsDialog::accept);

    auto* closeRow = new QHBoxLayout;
    closeRow->addStretch();
    closeRow->addWidget(closeButton);

    auto* layout = new QVBoxLayout(this);
    layout->addWidget(m_table);
    layout->addLayout(buttonRow);
    layout->addLayout(closeRow);

    refresh();
}

void PlanPaymentsDialog::refresh() {
    m_model->removeRows(0, m_model->rowCount());
    for (const auto& payment : m_workingCopy.payments) {
        QList<QStandardItem*> row;
        auto* dateItem = new QStandardItem(formatDate(payment.date));
        auto* amountItem = new QStandardItem(QString("$%1").arg(payment.amount, 0, 'f', 2));
        row << dateItem << amountItem;
        m_model->appendRow(row);
    }
    m_table->resizeColumnsToContents();
}

void PlanPaymentsDialog::addPayment() {
    PaymentDialog dialog(this);
    if (dialog.exec() == QDialog::Accepted) {
        m_workingCopy.payments.append(dialog.resultData());
        refresh();
    }
}

void PlanPaymentsDialog::editPayment() {
    const int row = m_table->currentIndex().row();
    if (row < 0 || row >= m_workingCopy.payments.size()) return;
    PaymentDialog dialog(this, &m_workingCopy.payments[row]);
    if (dialog.exec() == QDialog::Accepted) {
        m_workingCopy.payments[row] = dialog.resultData();
        refresh();
    }
}

void PlanPaymentsDialog::deletePayment() {
    const int row = m_table->currentIndex().row();
    if (row < 0 || row >= m_workingCopy.payments.size()) return;
    if (QMessageBox::question(this, "Delete Payment", "Delete the selected payment?") == QMessageBox::Yes) {
        m_workingCopy.payments.removeAt(row);
        refresh();
    }
}

void PlanPaymentsDialog::onDoubleClicked(const QModelIndex&) {
    editPayment();
}

void PlanPaymentsDialog::accept() {
    if (m_plan) {
        *m_plan = m_workingCopy;
        QString error;
        if (!DataManager::instance().save(&error)) {
            QMessageBox::critical(this, "Save Error", error);
            return;
        }
        emit DataManager::instance().dataChanged();
    }
    QDialog::accept();
}

PlanAnalysisDialog::PlanAnalysisDialog(QWidget* parent)
    : QDialog(parent) {
    setWindowTitle("Plan Analysis");
    resize(560, 420);

    m_titleLabel = new QLabel;
    m_requiredLabel = new QLabel;
    m_availableLabel = new QLabel;
    m_statusLabel = new QLabel;
    m_timeframeLabel = new QLabel;
    m_breakdownLabel = new QLabel;
    m_adviceEdit = new QTextEdit;
    m_adviceEdit->setReadOnly(true);

    auto* form = new QFormLayout;
    form->addRow("Item", m_titleLabel);
    form->addRow("Required Weekly Savings", m_requiredLabel);
    form->addRow("Available Weekly Funds", m_availableLabel);
    form->addRow("Status", m_statusLabel);
    form->addRow("Timeframe", m_timeframeLabel);
    form->addRow("Payment Breakdown", m_breakdownLabel);

    auto* layout = new QVBoxLayout(this);
    layout->addLayout(form);
    layout->addWidget(new QLabel("Advice"));
    layout->addWidget(m_adviceEdit);

    auto* closeButtons = new QDialogButtonBox(QDialogButtonBox::Close);
    connect(closeButtons, &QDialogButtonBox::rejected, this, &PlanAnalysisDialog::reject);
    connect(closeButtons, &QDialogButtonBox::accepted, this, &PlanAnalysisDialog::accept);
    layout->addWidget(closeButtons);
}

void PlanAnalysisDialog::setPlan(const PlanItem& plan) {
    m_plan = plan;
    refreshAnalysis();
    disconnect(&DataManager::instance(), nullptr, this, nullptr);
    connect(&DataManager::instance(), &DataManager::dataChanged, this, &PlanAnalysisDialog::refreshAnalysis);
}

void PlanAnalysisDialog::refreshAnalysis() {
    m_titleLabel->setText(m_plan.name);
    m_requiredLabel->setText(QString("$%1").arg(m_plan.weeklyPayment, 0, 'f', 2));

    double weeklyIncome = 0.0;
    double weeklyBills = 0.0;
    const auto& userData = DataManager::instance().data();
    for (const auto& income : userData.incomes) {
        weeklyIncome += income.weeklyAmount();
    }
    for (const auto& bill : userData.weeklyBills) {
        weeklyBills += bill.weeklyAmount();
    }
    for (const auto& bill : userData.monthlyBills) {
        weeklyBills += bill.weeklyAmount();
    }
    const double available = weeklyIncome - weeklyBills;
    m_availableLabel->setText(QString("$%1").arg(available, 0, 'f', 2));

    const bool affordable = available >= m_plan.weeklyPayment;
    m_statusLabel->setText(affordable ? "Affordable" : "Not Affordable");
    m_statusLabel->setStyleSheet(QString("font-weight: bold; color: %1;").arg(affordable ? "#107c10" : "#c50f1f"));

    m_timeframeLabel->setText(m_plan.timeframeString());
    m_breakdownLabel->setText(QString("Deposit $%1 + weekly $%2")
        .arg(m_plan.deposit, 0, 'f', 2)
        .arg(m_plan.weeklyPayment, 0, 'f', 2));

    QString advice;
    if (affordable) {
        advice = QString("Your available weekly funds are sufficient for this plan. You have $%1 spare per week.")
            .arg(available - m_plan.weeklyPayment, 0, 'f', 2);
    } else {
        advice = QString("This plan is not currently affordable. You are short by $%1 per week. Reduce expenses or increase income before committing.")
            .arg(m_plan.weeklyPayment - available, 0, 'f', 2);
    }
    if (m_plan.weeklyPayment <= 0.0) {
        advice += "\n\nNo weekly payment is set, so the goal can only be financed through the deposit.";
    }
    m_adviceEdit->setPlainText(advice);
}