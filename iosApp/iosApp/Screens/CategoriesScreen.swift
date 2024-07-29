//
//  CategoriesScreen.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 05/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared


struct CategoriesScreen: View {
    
    let onContinueClicked: ()-> Void
    @EnvironmentObject var sharedViewModel: SharedViewModel
    
    //contains category name, category emoji, category selection status
    private var categoryInfoList: [(String,String,Bool)] {
        get{
            sharedViewModel.categoryList.map {
                if sharedViewModel.selectedCategories.contains($0.categoryName){
                    ($0.categoryName, $0.categoryEmoji, true)
                } else {
                    ($0.categoryName, $0.categoryEmoji, false)
                }
            }
        }
    }
    var body: some View {
        
        VStack{
            Text("Select Categories")
                .padding(.top,40)
                .font(.largeTitle)
            
            Text("Select min 2 and max 5 categories")
            Spacer()
                
            
            GeometryReader(content: { geometry in
                generateCategoryChipUi(in: geometry)
            })
            .padding(.horizontal,30)
            .padding(.top, 30)
            
            Spacer()
            Button(action: {
                onContinueClicked()
            }){
                Text("Continue")
                    .frame(maxWidth: .infinity, alignment: .center)
                    .padding()
                    .background(Color.gray.opacity(0.25))
                    .cornerRadius(8)
            }
            .padding(.horizontal,50)
            .padding(.bottom,100)
            
            
        }
        .alert(sharedViewModel.selectedCategories.count < 2 ? "Select at least 2 categories" : "Maximum selection limit is 5 categories", isPresented: $sharedViewModel.showCategoryLimitAlert) {
            Button{
                sharedViewModel.showCategoryLimitAlert = false
            }label: {
                Text("OK")
            }
        }

    }
    
    private func generateCategoryChipUi(in geometry: GeometryProxy) -> some View {
        var width = CGFloat.zero
        var height = CGFloat.zero

        return ZStack(alignment: .topLeading) {
            ForEach(self.categoryInfoList, id: \.0) { item in
                self.chipUi(for: item)
                    .padding([.horizontal, .vertical], 4)
                    .alignmentGuide(.leading, computeValue: { d in
                        if (abs(width - d.width) > geometry.size.width) {
                            width = 0
                            height -= d.height
                        }
                        let result = width
                        if item == categoryInfoList.last! {
                            width = 0
                        } else {
                            width -= d.width
                        }
                        return result
                    })
                    .alignmentGuide(.top, computeValue: { _ in
                        let result = height
                        if item == categoryInfoList.last! {
                            height = 0
                        }
                        return result
                    })
            }
        }
    }

    private func chipUi(for categoryInfo: (String,String,Bool)) -> some View {
        
        CategoryChip(categoryInfo: categoryInfo){ name in
            if sharedViewModel.selectedCategories.contains(where: {$0 == name}){
                sharedViewModel.selectedCategories.removeAll(where: {$0 == name})
            }
            else {
                sharedViewModel.selectedCategories.append(name)
            }
        }
    }
    
}


struct CategoryChip: View{
    let categoryInfo: (String,String,Bool)
    let onTap : (String) -> Void
    
    var body: some View{
        HStack{
            Text(categoryInfo.1)
            Text(categoryInfo.0)
        }
        .padding(.all,10)
        .foregroundColor(.black)
        .background(categoryInfo.2 ? Color.black.opacity(0.1) : Color.white)
        .cornerRadius(10)
        .overlay(
            RoundedRectangle(cornerRadius: 10.0)
                .stroke(.black,lineWidth: categoryInfo.2 ? 0 : 1.5)
        )
        .onTapGesture {
            onTap(categoryInfo.0)
        }

    }
   
}


