#include "widgets/BaseTableWidget.h"

BaseTableWidget::BaseTableWidget(QWidget* parent)
    : QWidget(parent) {}

void BaseTableWidget::setupTable(const QStringList& headers) {
    auto* layout = new QVBoxLayout(this);
    m_table = new QTableView(this);
    m_model = new QStandardItemModel(this);
    m_model->setHorizontalHeaderLabels(headers);
    m_table->setModel(m_model);
    m_table->setSelectionBehavior(QAbstractItemView::SelectRows);
    m_table->setSelectionMode(QAbstractItemView::SingleSelection);
    m_table->setEditTriggers(QAbstractItemView::NoEditTriggers);
    m_table->horizontalHeader()->setStretchLastSection(true);
    m_table->verticalHeader()->setVisible(false);
    layout->addWidget(m_table);
}

void BaseTableWidget::setupButtons(const QString& addText, const QString& editText, const QString& deleteText) {
    auto* layout = qobject_cast<QVBoxLayout*>(this->layout());
    auto* row = new QHBoxLayout;
    m_addButton = new QPushButton(addText, this);
    m_editButton = new QPushButton(editText, this);
    m_deleteButton = new QPushButton(deleteText, this);
    row->addWidget(m_addButton);
    row->addWidget(m_editButton);
    row->addWidget(m_deleteButton);
    row->addStretch();
    layout->addLayout(row);
}

int BaseTableWidget::selectedRow() const {
    const QModelIndex index = m_table->currentIndex();
    return index.isValid() ? index.row() : -1;
}
