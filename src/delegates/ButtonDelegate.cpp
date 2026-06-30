#include "delegates/ButtonDelegate.h"

#include <QApplication>
#include <QMouseEvent>
#include <QPainter>

ButtonDelegate::ButtonDelegate(const QString& text, QObject* parent)
    : QStyledItemDelegate(parent), m_text(text) {}

void ButtonDelegate::paint(QPainter* painter, const QStyleOptionViewItem& option, const QModelIndex&) const {
    QRect rect = option.rect.adjusted(10, 4, -10, -4);
    QColor bgColor = (option.state & QStyle::State_MouseOver) ? QColor("#1d4ed8") : QColor("#2563eb");

    painter->save();
    painter->setRenderHint(QPainter::Antialiasing);
    painter->setBrush(bgColor);
    painter->setPen(Qt::NoPen);
    painter->drawRoundedRect(rect, 8, 8);
    painter->setPen(Qt::white);
    painter->drawText(rect, Qt::AlignCenter, m_text);
    painter->restore();
}

bool ButtonDelegate::editorEvent(QEvent* event, QAbstractItemModel*, const QStyleOptionViewItem& option, const QModelIndex& index) {
    if (event->type() == QEvent::MouseButtonRelease) {
        auto* mouse = static_cast<QMouseEvent*>(event);
#if QT_VERSION >= QT_VERSION_CHECK(6, 0, 0)
        const QPoint point = mouse->position().toPoint();
#else
        const QPoint point = mouse->pos();
#endif
        if (option.rect.contains(point)) {
            emit clicked(index);
            return true;
        }
    }
    return false;
}