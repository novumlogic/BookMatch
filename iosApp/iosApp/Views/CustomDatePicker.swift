//
//  CustomDatePicker.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 26/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//


import SwiftUI

import SwiftUI

struct CustomDatePicker: View {
    @Binding var selectedDate: Date?
    let selectableDates: [Date]
    
    @State private var displayedMonth: Date = Date()

    private var calendar: Calendar {
        Calendar.current
    }
    
    private var monthYearFormatter: DateFormatter {
        let formatter = DateFormatter()
        formatter.dateFormat = "MMMM yyyy"
        return formatter
    }
    
    private var dayFormatter: DateFormatter {
        let formatter = DateFormatter()
        formatter.dateFormat = "d"
        return formatter
    }
    
    private var daysOfWeek: [String] {
        calendar.shortWeekdaySymbols
    }
    
    private var datesInMonth: [Date] {
        guard let monthInterval = calendar.dateInterval(of: .month, for: displayedMonth) else {
            return []
        }
        
        var dates: [Date] = []
        var currentDate = monthInterval.start
        
        while currentDate < monthInterval.end {
            dates.append(currentDate)
            currentDate = calendar.date(byAdding: .day, value: 1, to: currentDate)!
        }
        
        return dates
    }
    
    var body: some View {
        VStack {
            HStack {
                Spacer()
                Button(action: {
                    displayedMonth = calendar.date(byAdding: .month, value: -1, to: displayedMonth) ?? Date()
                }) {
                    Image(systemName: "chevron.left")
                }
                Spacer()
                Text(monthYearFormatter.string(from: displayedMonth))
                    .font(.headline)
                Spacer()
                Button(action: {
                    displayedMonth = calendar.date(byAdding: .month, value: 1, to: displayedMonth) ?? Date()
                }) {
                    Image(systemName: "chevron.right")
                }
                Spacer()
            }
            
            LazyVGrid(columns: Array(repeating: .init(.flexible()), count: 7), spacing: 5) {
                ForEach(daysOfWeek, id: \.self) { day in
                    Text(day)
                        .font(.subheadline)
                        .bold()
                }
                .padding(.vertical,10)
                
                ForEach(datesInMonth, id: \.self) { date in
                    Text(dayFormatter.string(from: date))
                        .padding(8)
                        .background(selectableDates.contains(date) ? (selectedDate == date ? Color.blue : Color.gray.opacity(0.2)) : Color.clear)
                        .cornerRadius(8)
                        .foregroundStyle(selectableDates.contains(date) ? (selectedDate == date ? .white : .primary) : .secondary)
                        .onTapGesture {
                            if selectableDates.contains(date) {
                                selectedDate = date
                            }
                        }
                }
            }
        }
     
    }
}

