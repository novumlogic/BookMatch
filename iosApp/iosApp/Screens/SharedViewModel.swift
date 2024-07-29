//
//  HomeViewModel.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 15/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared
import GoogleSignIn

extension UserDefaults{
    func setStringArray(_ array: [String], forKey key: String, updateRemote: Bool = true){
        let categoriesStr = array.joined(separator: ",")
        if updateRemote {
            Task{
                do{
                    let userId = try await SupabaseProvider.shared.getCurrentUserId()
                    try await RemoteDataSource.shared.updateSelectedCategories(userId: userId, categories: categoriesStr)
                }catch{
                    
                }
            }
        }
        let jsonData = try? JSONEncoder().encode(array)
        set(jsonData, forKey: key)
    }
    
    func stringArray(forKey key: String) -> [String]? {
        guard let jsonData = data(forKey: key) else { return nil }
        return try? JSONDecoder().decode([String].self, from: jsonData)
    }
}



final class SharedViewModel: ObservableObject{
    
    init(){
        self._lastRecommendationTime = UserDefaults.standard.object(forKey: "lastRecommendationTime") as? Int64 ?? 0
        Task{
            if isLoggedIn {
                await fetchChatHistory()
            }
            await fetchCategories()
        }
    }
    
    func reloadInit(){
        Task{
            if isLoggedIn {
                await fetchChatHistory()
            }
            await fetchCategories()
        }
    }
    
    @AppStorage("isLoggedIn") var isLoggedIn : Bool = false
    
    private var _lastRecommendationTime: Int64 {
        didSet {
            UserDefaults.standard.set(NSNumber(value: _lastRecommendationTime), forKey: "lastRecommendationTime")
        }
    }
    
    // Public getter for lastRecommendationTime
    var lastRecommendationTime: Int64 {
        get {
            (_lastRecommendationTime != 0) ? _lastRecommendationTime : UserDefaults.standard.object(forKey: "lastRecommendationTime") as? Int64 ?? 0
        }
    }
    
//    var lastRecommendationTime : Int64 {
//        get{
//            (UserDefaults.standard.object(forKey: "lastRecommendationTime") as? NSNumber)!.int64Value
//        }
//        
//        set {
//            UserDefaults.standard.set(NSNumber(value: Int64(newValue)), forKey: "lastRecommendationTime")
//        }
//        
//    }
    
    @AppStorage("categoryShown") private(set) var categoryShown = false
    @Published var selectedCategories = UserDefaults.standard.stringArray(forKey: "selectedCategories") ?? []
    
    @Published var tabSelection = 1
    @Published var categoryList : [Categories] = [] 
    private var categoryNameId: [String:Int32] = [:]
    
    @Published var showCategoryLimitAlert = false
    @Published var fetchSource = FetchSource.supabase
    @Published var failedReason = FailedReason.none
    @Published var bookRecommendations : [String: [BookDetails]] = [:]
    @Published var previousSelectedCategories: [String] = []
    @Published var timestampIdDict: [Int64 : Int32] = [:]
    
    func fetchCategories() async {
        
        do{
            let result = try await RemoteDataSource.shared.fetchCategories()
            
            if let result {
                DispatchQueue.main.async {
                    self.categoryList = result
                }
                for c in self.categoryList{
                    self.categoryNameId[c.categoryName.lowercased()] = c.categoryId
                }
                
            }else{
                self.categoryList = []
                print("fetchCategories: Fetch Failed")
            }
        }catch{
            print("Error fetchCategories: \(error)")
        }
    }
    
    func fetchChatHistory() async{
        do{
            let userId = try await SupabaseProvider.shared.getCurrentUserId()
            let result = try await RemoteDataSource.shared.fetchChatHistory(userId: userId)
            
            if let result{
                OpenAIClient.shared.clearChatHistory()
                result.forEach { chatHistory in
                    OpenAIClient.shared.addUserMessage(userMessage: chatHistory.userText)
                }
            }else{
                print("fetchChatHistory: Fetch failed")
            }
            
        }catch{
            print("Error fetchCategories: \(error)")
        }
    }
    
    
    func setCategoryShown(value: Bool, updateRemote: Bool = true) async{
        do{
            let userId = try await SupabaseProvider.shared.getCurrentUserId()
            if updateRemote {
                try await RemoteDataSource.shared.updateCategoryShown(userId: userId, value: value)
            }
            DispatchQueue.main.async {
                self.categoryShown = value
            }
            
        }catch{
            
        }
        
    }
    
    func fetchRecommendationTimestamps() async -> [Recommendations]? {
        do{
            let userId = try await SupabaseProvider.shared.getCurrentUserId()
            return try await RemoteDataSource.shared.fetchRecommendationTimestamp(userId: userId)
        }catch{
            return nil
        }
    }
    
    func setLastRecommendationTime(lastRecommendationTime: Int64, updateRemote: Bool = true) async {
        do{
            let userId = try await SupabaseProvider.shared.getCurrentUserId()
            if updateRemote{
                try await RemoteDataSource.shared.updateLastRecommendationTime(userId: userId, timestamp: lastRecommendationTime)
            }
            _lastRecommendationTime = lastRecommendationTime
            
        }catch{
            
        }
    }
    


    
    func signIn() {
        GIDSignIn.sharedInstance.signIn(withPresenting: getRootViewController()){
            signInResult, error in
  
            guard error == nil else {  print("Error signing in: \(String(describing: error?.localizedDescription))")
                return }
            guard let signInResult else { return }

            signInResult.user.refreshTokensIfNeeded { user, error in
                guard error == nil else { return }
                guard let user else { return }
                
//                let accessToken = user.accessToken.tokenString
                guard let idToken = user.idToken?.tokenString else {
                   print("No idToken found.")
                   return
                 }
                Task{
                    do{
//                        try await self.client.auth.signInWithIdToken(
//                            credentials: OpenIDConnectCredentials(
//                                provider: .google,
//                                idToken: idToken,
//                                accessToken: accessToken
//                            )
//                        )
                        
                        try await SupabaseProvider.shared.signInWithToken(token: idToken)
                        DispatchQueue.main.async{
                            self.onLogin()
                            self.isLoggedIn = true
                        }
                        
                    }catch{
                        print("Error while signing in \(error)")
                    }
                }
            }

        }

     }

    private func onLogin()  {
        guard let userInfo = SupabaseProvider.shared.getCurrentUserOrNull() else {
            print("The current user is not present ")
            return
        }
        Task{
        do{
            let user = try await RemoteDataSource.shared.userExists(userId: userInfo.id)
            if user == nil {
                let newUser = SupabaseProvider.shared.decodeUser(userInfo: userInfo)
                try await RemoteDataSource.shared.createUser(users: newUser)
            }else{
                if let categoryShown = user?.categoryShown {
                    await self.setCategoryShown(value: categoryShown.boolValue, updateRemote: false)
                }
                if let lastRecommendationTime = user?.lastRecommendationTime{
                    await self.setLastRecommendationTime(lastRecommendationTime: lastRecommendationTime.int64Value, updateRemote: false)
                }
                if let selectedCategories = user?.selectedCategories{
                    let array = selectedCategories.components(separatedBy: ",")
                    UserDefaults.standard.setStringArray(array, forKey: "selectedCategories", updateRemote: false)
                }
            }
            }catch{
            print("Error while checking and creating the user \(error)")
        }

        }
        
//        DispatchQueue.main.async{
//            RemoteDataSource.shared.userExists(userId: user.id.uuidString) { result, error in
//                print("The result was \(result) and error was \(error)")
//                guard let result, error == nil else {
//                    return
//                }
//                if result == true {
//                    return
//                } else{
//                    let newUser = Users(userId: user.id.uuidString, email: user.email ?? "", displayName: user.userMetadata["name"] as? String ?? "" , avatarUrl: user.userMetadata["picture"] as? String ?? "", createdAt: "")
//    
//                    RemoteDataSource.shared.createUser(users: newUser) { result ,error  in
//                        print("The result is \(result) and error = \(error)")
//                        guard let result else{
//                            return
//                        }
//    
//                    }
//                }
//            }
//        }
        
    }
    
    func isSessionValid() async throws  {
        let session = try await SupabaseProvider.shared.loadSession()
        if session == nil {
            try await signOut()
        }
    }
    
     func signOut() async throws {
         DispatchQueue.main.async {
             self.isLoggedIn = false
             self.fetchSource = .ai
             self.categoryShown = false
             self.tabSelection = 0
         }
         try await SupabaseProvider.shared.signOut()
         GIDSignIn.sharedInstance.signOut()
     }
    
    
    
     private func getRootViewController() -> UIViewController {
         guard let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
               let rootViewController = scene.windows.first?.rootViewController else {
             fatalError("Unable to get root view controller")
         }
         return rootViewController
     }
    
    
    func fetchBookFromDb(lastRecommendationTime: Int64) async  -> RecommendationStatus {
        var status = RecommendationStatus.failed
        do{
            let userId = try await SupabaseProvider.shared.getCurrentUserId()
            let result = try await RemoteDataSource.shared.fetchBooks(userId: userId, lastRecommendationTime: lastRecommendationTime)
            
            if let result {
                DispatchQueue.main.async {
                    self.bookRecommendations = result
                }
                status = .loaded
            }else{
                status = .failed
            }
        }catch{
            print("Error while fetching books from db: \(error)")
            status = .failed
        }
        return status
    }
    
    func getBookRecommendation(lastRecommendationTime: Int64) async -> RecommendationStatus{
        OpenAIClient.shared.clearChatHistory()
        let categoriesStr = selectedCategories.joined(separator: ",")
        var status = RecommendationStatus.failed
        do{
            let result = try await OpenAIClient.shared.generateContent(text: categoriesStr)
            
            if let result{
                DispatchQueue.main.async {
                    self.bookRecommendations = result
                }
                var bookHistoryList: [BookHistory] = []
                
                for(key,value) in result{
                    bookHistoryList.append(BookHistory(genre: key, list: value))
                }
                
                insertBooks(bookHistoryList: bookHistoryList, lastRecommendationTime: lastRecommendationTime)
                let userId = try await SupabaseProvider.shared.getCurrentUserId()
                let modelMessage = getModelMessage(bookHistoryList: bookHistoryList)
                let chatHistory = ChatHistory(userId: userId, userText: categoriesStr, aiAnswer: modelMessage, timestamp: lastRecommendationTime, id: 0)
                
                try await RemoteDataSource.shared.addChatHistory(chatHistory: chatHistory)
                await setLastRecommendationTime(lastRecommendationTime: lastRecommendationTime)
                
                status = .loaded
            } else{
                
                DispatchQueue.main.async {
                    self.bookRecommendations = [:]
                }
                status = .failed
            }
            
        }catch{
            print("The error \(error) occurred while fetching bookrecommendation")
            status = .failed
        }
        
        return status
        
    }
    
    func getModelMessage(bookHistoryList: [BookHistory]) -> String {
        return bookHistoryList.map { bookHistory in
            let books = bookHistory.list.map { $0.bookName }.joined(separator: ", ")
            return " \(bookHistory.genre) : [ \(books) ] "
        }.joined(separator: ", ")
    }

    private func insertBooks(bookHistoryList: [BookHistory], lastRecommendationTime: Int64) {
                
        Task{
            var bookList: [Books] = []
            
            for bookHistory in bookHistoryList{
                let categoryId = categoryNameId[bookHistory.genre.lowercased()] ?? 1
                for books in bookHistory.list {
                    bookList.append(Books(bookName: books.bookName, authorName: books.authorName, genreTags: books.genreTags, categoryId: categoryId, description: books.description_, pages: books.pages, isbn: books.isbn, firstDateOfPublication: books.firstDateOfPublication, referenceLink: books.referenceLink, id: 0))
                }
            }
            
            do{
                let insertResult = try await RemoteDataSource.shared.addBooks(books: bookList)
                
                if let bulkInsertResult = insertResult{
                    print(" The inserted rows count = \(bulkInsertResult.insertedBooksList.count) and description = \(bulkInsertResult.insertedBooksList.description) \n duplicated rows count \(bulkInsertResult.duplicateBookList.count) and description = \(bulkInsertResult.duplicateBookList.description)")
                    
                    let userId = try await SupabaseProvider.shared.getCurrentUserId()
                    guard !userId.isEmpty else { return }
                    
                    let recommendationsInsert = try await RemoteDataSource.shared.addRecommendation(recommendations: Recommendations(userId: userId, timestamp: lastRecommendationTime, id: 0))
                    
                    if let recommendation =  recommendationsInsert{
                        
                        let recommendedBookList = (bulkInsertResult.insertedBooksList + bulkInsertResult.duplicateBookList).map{
                            RecommendedBooks(recommendationId: recommendation.id, bookId: $0.bookId, id: 0)
                        }
                        
                        let recommendedBookResult = try await RemoteDataSource.shared.addRecommendedBooks(recommendedBookDtos: recommendedBookList)
                        
                        if let recommendedBookResult {
                            let rMap = Dictionary(uniqueKeysWithValues: recommendedBookResult.map { ($0.bookId, $0) })
                            let bMap = Dictionary(uniqueKeysWithValues: (bulkInsertResult.insertedBooksList + bulkInsertResult.duplicateBookList).map { ($0.bookName, $0) })
                            
                            for(_,bookDetailsList) in bookRecommendations{
                                for bookDetails in bookDetailsList{
                                    if let bookId = bMap[bookDetails.bookName]?.bookId, let recommendedBookId = rMap[bookId]?.id {
                                        bookDetails.recommendedBookId = KotlinInt(int: recommendedBookId)
                                    }
                                }
                            }
                            
                        }else{
                            print("The insertion operation of recommended book list failed ")
                        }
                        
                    }else{
                        print("The recommendation insertion operation failed")
                    }
                    
                }else{
                    print("The bulk insertion operation failed ")
                }
            }catch{
                print("Error insertBooks function: \(error)")
            }
        }
    }
    
    
    func recommendMore(liked: [String], disliked: [String], rating: [String: Int], totalBooks: [String], lastRecommendationTime: Int64) async -> RecommendationStatus{
        var userText: [String] = []
        let text = (liked.isEmpty && disliked.isEmpty && rating.isEmpty) ? {
            """
            Suggest me more books in genres: [ \(self.selectedCategories.joined(separator: ",")) ], do not repeat these books: \(totalBooks.joined(separator: ","))
            """
        } : {
            var remainingBooks = totalBooks
            userText.append("Suggest me more books in these [ \(self.selectedCategories.joined(separator: ",")) ] ")
            
            if !rating.isEmpty{
                userText.append(" My rating \(rating.description) ")
                remainingBooks.removeAll { book in
                    rating.keys.contains(book)
                }
            }
            
            if !disliked.isEmpty {
                userText.append("I dislike [ \(disliked.joined(separator: ",")) ] ")
                remainingBooks.removeAll{ disliked.contains($0) }
            }
            
            if !liked.isEmpty{
                userText.append("I like [ \(liked.joined(separator: ",")) ] ")
                remainingBooks.removeAll(where: {
                    liked.contains($0)
                })
            }
            
            if !remainingBooks.isEmpty{
                userText.insert("Do not repeat the mentioned books and \(remainingBooks.joined(separator: ","))", at: userText.count-1)
            } else {
                userText.insert("Do not repeat the mentioned books.", at: userText.count-1)
            }
            
            return userText.joined(separator: ". ")
        }
        
        var status = RecommendationStatus.failed
        do{
            let result = try await OpenAIClient.shared.generateContent(text: text())
            
            if let result {
                bookRecommendations = result
                var bookHistoryList: [BookHistory] = []
                for(key,value) in result {
                    bookHistoryList.append(BookHistory(genre: key, list: value))
                }
                
                let modelMessage = getModelMessage(bookHistoryList: bookHistoryList)
                insertBooks(bookHistoryList: bookHistoryList, lastRecommendationTime: lastRecommendationTime)
                
                let userId = try await SupabaseProvider.shared.getCurrentUserId()
                
                guard !userId.isEmpty else {return .failed}
                
                let chatHistory = ChatHistory(userId: userId, userText: text(), aiAnswer: modelMessage, timestamp: lastRecommendationTime, id: 0)
                
                try await RemoteDataSource.shared.addChatHistory(chatHistory: chatHistory)
                await setLastRecommendationTime(lastRecommendationTime: lastRecommendationTime)
                
                status = .loaded
            }else {
                print("recommend More: operation failed")
                DispatchQueue.main.async{
                    self.bookRecommendations = [:]                    
                }
                status = .failed
            }
        }catch{
            status = .failed
            print("Error recommend more: \(error)")
        }
        
        
        return status
    }
    
    func fetchBookFromRecommendationId(recommendationId: Int32, timestamp: Int64) async -> RecommendationStatus{
        var status = RecommendationStatus.failed
        do {
            let chatHistoryResult = try await RemoteDataSource.shared.fetchChatHistory(timestamp: timestamp)
            let booksResult = try await RemoteDataSource.shared.fetchBooks(recommendationId: recommendationId)
            if let booksResult{
                if let chatHistoryResult {
                    OpenAIClient.shared.changeChatHistory(list: chatHistoryResult)
                } else {
                    print("FetchBookFromRecommendationId: failed to change chat history ")
                }
                DispatchQueue.main.async {
                    self.bookRecommendations = booksResult
                }
                status = .loaded
            }else{
                print("FetchBookFromRecommendationId: books not fetched ")
                status = .failed
            }
        }catch{
            status = .failed
        }
        return status
    }
}


 
