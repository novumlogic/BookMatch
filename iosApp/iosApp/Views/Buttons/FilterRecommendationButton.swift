//
//  FilterRecommendationButton.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 24/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct FilterRecommendationButton: View {
    
    var currentRecommendationTimestamp: Int64
    @State private var date: Date?
    @State private var showDatePicker = false
    @State private var selectedTimestamps: [Int64] = []
    
    @State private var selectableDates: [Date] = []
    
    @EnvironmentObject var sharedViewModel: SharedViewModel
    
    // Callback to pass selected timestamp, recommendation id
    let onSelectRecommendationId: (Int64,Int32) -> Void
    
    var body: some View {
        HStack {
            Spacer()
            Button {
                showDatePicker = true
            } label: {
                HStack {
                    Label("Filter", systemImage: "line.3.horizontal.decrease.circle")
                        .imageScale(.large)
                }
            }
            .padding(.trailing, 20)
            .sheet(isPresented: $showDatePicker, content: {
                VStack {
                    HStack {
                        Spacer()
                        Button {
                            showDatePicker = false
                        } label: {
                            Image(systemName: "xmark")
                                .imageScale(.large)
                        }
                    }
                    .padding()
                    Text("Choose from previous recommendations")
                        .font(.title2)
                        .multilineTextAlignment(.center)
                        .padding()
                    
                    CustomDatePicker(selectedDate: $date, selectableDates: selectableDates)
                        .padding(.horizontal, 20)
                        .onChange(of: date) { newDate in
                            if let newDate = newDate {
                                updateSelectedHours(for: newDate)
                            }
                        }
                        .onAppear(perform: {
                            updateSelectableDates()
                        })
                    
                    
                    if(!selectedTimestamps.isEmpty){
                        List(selectedTimestamps, id: \.self) { timestamp in
                                Button{
                                    onSelectRecommendationId(timestamp, sharedViewModel.timestampIdDict[timestamp]!)
                                    showDatePicker = false
                                } label: {
                                    Text(formatTimestampToHourMin(timestamp))
                                }
                                .listRowBackground(currentRecommendationTimestamp == timestamp ? Color.theme.secondaryContainer : .white)
                        }
                    }
                    
                    Spacer()
                }
            })
            .onReceive(sharedViewModel.$bookRecommendations, perform: { _ in
                updateSelectableDates()
            })

            
        }
        .padding(.top, 20)
    }
    
    private func formatTimestampToHourMin(_ timestamp: Int64) -> String {
        // Convert the Unix timestamp to Date
        let date = Date(timeIntervalSince1970: TimeInterval(timestamp / 1000)) // Ensure timestamp is in seconds
        
        // Create a DateFormatter
        let dateFormatter = DateFormatter()
        dateFormatter.dateStyle = .none
        dateFormatter.timeStyle = .long
        dateFormatter.locale = Locale.current // Use the current locale of the device
        
        // Format the date
        return dateFormatter.string(from: date)
    }

    
    private func updateSelectableDates() {
        let calendar = Calendar.current
       
        let timestamps = sharedViewModel.timestampIdDict.keys
        selectableDates = timestamps.map { Date(timeIntervalSince1970: TimeInterval($0 / 1000)) }.map{ calendar.startOfDay(for: $0)}
        // Ensure unique dates
        selectableDates = Array(Set(selectableDates)).sorted()
        print("The dates = \(selectableDates)")
    }
    
    private func updateSelectedHours(for date: Date) {
        let calendar = Calendar.current
        let startOfDay = calendar.startOfDay(for: date)
        let endOfDay = calendar.date(byAdding: .day, value: 1, to: startOfDay)!
        
        // Clear previous hours and mappings
        selectedTimestamps.removeAll()
        
        // Filter timestamps for the selected date
        for (timestamp, _) in sharedViewModel.timestampIdDict {
            let timestampDate = Date(timeIntervalSince1970: TimeInterval(timestamp/1000))
            
            print("The dates in timestamp dictionary \(timestampDate) , startday = \(startOfDay) , endofday = \(endOfDay)")
            
            if timestampDate >= startOfDay && timestampDate < endOfDay {
                // Add hour if it's within the selected day
                if !selectedTimestamps.contains(timestamp) {
                    selectedTimestamps.append(timestamp)
                }
            }
        }
        
        // Sort hours
        selectedTimestamps.sort()
    }
}

#Preview {
    FilterRecommendationButton(currentRecommendationTimestamp: 0){ _, _ in
            
    }.environmentObject(SharedViewModel())
}
