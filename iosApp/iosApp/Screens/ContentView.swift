import SwiftUI
import Shared

struct ContentView: View {
    
    @State private var isActive = false
    @EnvironmentObject var sharedViewModel: SharedViewModel
    var body: some View {
        if isActive{
            if sharedViewModel.isLoggedIn {
                if !sharedViewModel.categoryShown {
                    CategoriesScreen(onContinueClicked: {
                        if sharedViewModel.selectedCategories.count >= 2 && sharedViewModel.selectedCategories.count <= 5 {
                            sharedViewModel.tabSelection = 1
                            Task{
                                await sharedViewModel.setCategoryShown(value: true)                             
                            }
                            
                            sharedViewModel.fetchSource = .ai
                            
                            UserDefaults.standard.setStringArray(sharedViewModel.selectedCategories, forKey: "selectedCategories")
                        } else{
                            sharedViewModel.showCategoryLimitAlert = true
                        }
                        
                    })
                } else{
                    HomeScreen()
                }
            } else {
                LoginScreen()
                    .background(Color.theme.lightOrange)
                    .ignoresSafeArea()
            }
        }else{
            SplashScreen(isActive: $isActive, onSplashShown: {
                Task{
                    do {
                        try await sharedViewModel.isSessionValid()
                    } catch{
                        print("The error while checking session is valid \(error)")
                    }
                }
            })            
        }

    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
            .environmentObject(SharedViewModel())
    }
}
