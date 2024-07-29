//
//  ReadStatusButton.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 25/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI


struct ReadStatusButton: View{
    let readStatus : Int
    let onRead: (Int) -> Void
    
    let segments = ["Read", "Unread"]
    var body: some View{
//        Picker("Select", selection: $readStatus){
//            ForEach(0..<segments.count){
//                index in
//                Text(segments[index])
//                    .tag(index)
//            }
//        }
//        .pickerStyle(SegmentedPickerStyle())
//        .colorMultiply(Color.theme.secondaryContainer)
//        .padding()
        
        HStack{
            Button{
                onRead(0)
            } label: {
                if readStatus == 0 {
                    Image(systemName: "checkmark")
                }
                Text("Read")
            }
            .foregroundStyle(.black)
            .padding(.horizontal,30)
            .padding(.vertical, 12)
            
            Divider().frame(width: 2)
                .frame(maxHeight: /*@START_MENU_TOKEN@*/.infinity/*@END_MENU_TOKEN@*/)
                .background(Color.theme.outline)
            
            Button{
               onRead(1)
            }label: {
                if readStatus == 1 {
                    Image(systemName: "checkmark")
                }
                Text("Unread")
            }
            .foregroundStyle(.black)
            .padding(.horizontal,30)
            .padding(.vertical, 12)
            
        }
        
        .overlay(
            RoundedRectangle(cornerRadius: 30)
            .stroke(Color.theme.outline, lineWidth: 2)
        )
        
        .frame(width: 300, height: 50)
    }
}

#Preview {
    ReadStatusButton(readStatus: 1) { _ in
        
    }
}
