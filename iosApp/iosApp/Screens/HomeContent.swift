//
//  HomeContent.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 26/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct HomeContent: View{

    @ObservedObject var sharedViewModel: SharedViewModel
    @ObservedObject var homeViewModel: HomeViewModel
    @ObservedObject var networkViewModel: NetworkViewModel
    
    var sortedBookMap: [String] {
        sharedViewModel.bookRecommendations.keys.sorted()
    }
    @State private var showBookDetails = false
    @State private var bookToShow : BookDetails = BookDetails(
        bookName: "Demo Book",
        authorName: "Author F",
        genreTags: ["Horror", "Psychological"],
        description: "Description of Demo Book 3",
        pages: "280",
        isbn: "9781847494870",
        firstDateOfPublication: "2017-03-14",
        referenceLink: "http://example.com/demo",
        categoryId: 2,
        categoryName: "Horror",
        recommendedBookId: 203,
        liked: true,
        rating: 3,
        read: true,
        lastUpdatedTime: Int64(Date().timeIntervalSince1970 * 1000)
    )
    
    @State private var bookList : [BookDetails] = []
    @State private var recommendationStatus = RecommendationStatus.loaded
    
    @State private var showLogoutAlert = false
    @State private var failedReason = FailedReason.none
    @State private var reloadRecommendations = false
    @State private var reloadCategoriesAndHistory = false
    @State private var currentRecommendationTimestamp: Int64 = 0
    
    
    var body: some View {
        
        ZStack {
            NavigationStack{
                VStack{
                    ScrollView(showsIndicators: false) {
                        FilterRecommendationButton(currentRecommendationTimestamp: currentRecommendationTimestamp ) { timestamp, id in
                            recommendationStatus = .loading
                            Task{
                                recommendationStatus = await sharedViewModel.fetchBookFromRecommendationId(recommendationId: id, timestamp: timestamp)
                            }
                            currentRecommendationTimestamp = timestamp
                        }

                        ForEach(sortedBookMap, id: \.self){ genre in
                            if let books = sharedViewModel.bookRecommendations[genre]{
                                if !books.isEmpty{
                                    GenreBookView(title: genre){
                                        HorizontalBookScrollView(books: books,genre: genre)
                                    }
                                }
                            }
                        }
                        
                    }
                    
                    CustomTabBar {
                        recommendationStatus = .loading
                        
                        Task{
                            var totalBooks: [String] = []
                            
                            for (_,books) in sharedViewModel.bookRecommendations{
                                for book in books{
                                    totalBooks.append(book.bookName)
                                }
                            }
                            
                            var ratings: [String: Int] = [:]
                            for(_,value) in homeViewModel.ratingDict {
                                ratings[value.0] = value.1
                            }
                            
                            self.recommendationStatus = await sharedViewModel.recommendMore(liked: Array(homeViewModel.likedBookDict.values), disliked: Array(homeViewModel.dislikedBookDict.values), rating: ratings, totalBooks: totalBooks, lastRecommendationTime: Int64(Date().timeIntervalSince1970 * 1000))
                            
                            failedReason = .aiError
                        }
                    }
                    .background(Color.theme.secondaryContainer)
                    
                }
                .toolbar {
                    ToolbarItem(placement: .principal) {
                        Text("Match Book")
                            .font(.largeTitle)
                                              
                    }
                    
                    ToolbarItem(placement: .destructiveAction){
                        Button{
                           showLogoutAlert = true
                        } label: {
                            Image(systemName: "rectangle.portrait.and.arrow.right")
                                .foregroundStyle(.black)
                        }
                    }
                    
                }
                .navigationBarTitleDisplayMode(.inline)
                .navigationDestination(isPresented: $showBookDetails) {
                    BookDetailScreen(book: $bookToShow, bookList: bookList, onRead: { recommendedBookId , value in
                        
                        homeViewModel.updateReadStatus(recommendedBookId: recommendedBookId, status: value, currentTimeMillis: Int64(Date().timeIntervalSince1970 * 1000))
                        
                    }, onRatingChange: {recommendedBookId, bookName, rating in
                        if let rating {
                            homeViewModel.ratingDict[recommendedBookId] = (bookName, rating)
                        } else{
                            homeViewModel.ratingDict.removeValue(forKey: recommendedBookId)
                        }
                        
                        homeViewModel.updateRatings(recommendedBookId: recommendedBookId, rating: rating, currentTimeMillis: Int64(Date().timeIntervalSince1970 * 1000))
                    })
                }
                .alert(isPresented: $showLogoutAlert) {
                    Alert(title: Text("Are you sure you want to Logout?"), primaryButton: .destructive(Text("Yes"), action: {
                        Task{
                            do {
                                try await sharedViewModel.signOut()
                            } catch{
                                print("The error while signing out \(error)")
                            }
                        }
                    }), secondaryButton: .cancel(Text("No"), action: {
                        showLogoutAlert = false
                    }))
                }
                
            }
            .tint(.black)
            
            if(recommendationStatus == .loading){
                
                LoadingScreen(recommendationStatus: $recommendationStatus)
                
            } else if recommendationStatus == .failed{
                switch failedReason {
                case .noInternet:
                    EmptyView()
                        .onReceive(networkViewModel.$showAlert){
                            newValue in
                            if newValue{
                                reloadCategoriesAndHistory = true
                                sharedViewModel.fetchSource = .supabase
                                reloadRecommendations = true
                            }
                        }
                    
                case .aiError:
                    VStack{
                        Text("Something went wrong while generating output, please try again")
                            .multilineTextAlignment(.center)
                            .padding()
                        
                        Button{
                            reloadRecommendations = true
                        } label:{
                            Image(systemName: "arrow.clockwise")
                                .imageScale(.large)
                                .padding(20)
                                .overlay {
                                    Circle()
                                        .stroke(.blue)
                                }
                        }
                    }
                    
                case .none:
                    EmptyView()
                }
                  
            }
                
        }
        .onReceive(sharedViewModel.$selectedCategories) { newList in
            if newList != sharedViewModel.previousSelectedCategories {
                recommendationStatus = RecommendationStatus.loading
                Task{
                    recommendationStatus = await performRecommendationTask()
                }
                sharedViewModel.previousSelectedCategories = newList
            }
        }
        .onReceive(sharedViewModel.$bookRecommendations){ _ in
            Task{
                let result = await sharedViewModel.fetchRecommendationTimestamps()
                if let result {
                    sharedViewModel.timestampIdDict.removeAll()
                    for r in result {
                        sharedViewModel.timestampIdDict[r.timestamp] = r.id
                    }
                }
            }
        }
        .onChange(of: reloadRecommendations) { newValue in
            if newValue {
                recommendationStatus = .loading
                Task{
                    recommendationStatus = await performRecommendationTask()
                }
                reloadRecommendations = false
            }
        }
        .onChange(of: reloadCategoriesAndHistory) { newValue in
            if newValue {
                sharedViewModel.reloadInit()
                reloadCategoriesAndHistory = false
            }
            
        }
        .onAppear{
            currentRecommendationTimestamp = sharedViewModel.lastRecommendationTime
        }
        
        
    }
    
    @ViewBuilder
    private func HorizontalBookScrollView(books: [BookDetails], genre: String ) -> some View {
        ScrollView(.horizontal, showsIndicators: false){
            HStack{
                
                ForEach(books, id: \.bookName){
                    book in
                    Button{
                        bookList = books
                        bookToShow = book
                        showBookDetails = true
                    } label:{
                        BookItem(book: book) { recommendedBookId, bookName, value in
                            if(value == false){
                                homeViewModel.dislikedBookDict[recommendedBookId.intValue] = bookName
                                homeViewModel.likedBookDict.removeValue(forKey: recommendedBookId.intValue)
                            }
                            
                            else if value == true {
                                homeViewModel.likedBookDict[recommendedBookId.intValue] = bookName
                                homeViewModel.dislikedBookDict.removeValue(forKey: recommendedBookId.intValue)
                            }
                            
                            else {
                                homeViewModel.likedBookDict.removeValue(forKey: recommendedBookId.intValue)
                                homeViewModel.dislikedBookDict.removeValue(forKey: recommendedBookId.intValue)
                            }
                            homeViewModel.changeLikeDislike(recommendedBookId: recommendedBookId.int32Value, value: value?.boolValue, currentTimeMillis: Int64(Date().timeIntervalSince1970 * 1000))
                            
                        }
                        
                    }
                }

                .padding(.bottom, genre == sortedBookMap.last ? 100: 0)
            }
        }
    }
    
    private func performRecommendationTask() async -> RecommendationStatus{
        let currentTime = Int64(Date().timeIntervalSince1970 * 1000 )
        var status: RecommendationStatus

        
        if sharedViewModel.fetchSource == .supabase && sharedViewModel.lastRecommendationTime != 0 {
            status = await sharedViewModel.fetchBookFromDb(lastRecommendationTime: sharedViewModel.lastRecommendationTime)
            failedReason = .noInternet
        }else {
            status = await sharedViewModel.getBookRecommendation(lastRecommendationTime: currentTime)
            failedReason = .aiError
        }
        sharedViewModel.fetchSource = .ai
        return status
        
    }
}


