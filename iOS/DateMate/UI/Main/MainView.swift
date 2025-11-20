//
//  MainView.swift
//  DateMate
//
//  Created by 홍희표 on 2021/12/03.
//

import SwiftUI

struct MainView: View {
    @StateObject private var viewModel = MainViewModel()
    
    @State private var selectedTab = 0
    
    var onNavigateToSubFirst: (String) -> Void
    
    var onNavigateToSubSecond: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            // Content
            // TODO

            // Main Tab Content
            TabView(selection: $selectedTab) {
                HomeView(onNavigateToSubFirst: onNavigateToSubFirst)
                .tabItem {
                    Label("First", systemImage: "house")
                }
                .tag(0)
                LoungeView(onNavigateToSubSecond: onNavigateToSubSecond)
                .tabItem {
                    Label("Second", systemImage: "heart")
                }
                .tag(1)
            }
        }
        .navigationTitle("Datemate")
        .navigationBarTitleDisplayMode(.inline)
    }
}

/*struct MainView2: View {
    @State private var selectedTab = 0
    
    var onNavigate: (String) -> Void

    var body: some View {
        VStack(spacing: 0) {
            // Content
            // TODO

            // Main Tab Content
            TabView(selection: $selectedTab) {
                FirstView {
                    onNavigate("sub_first/Test")
                }
                .tabItem {
                    Label("First", systemImage: "house")
                }
                .tag(0)

                SecondView {
                    onNavigate("sub_second")
                }
                .tabItem {
                    Label("Second", systemImage: "heart")
                }
                .tag(1)
            }
        }
        .navigationTitle("Datemate")
        .navigationBarTitleDisplayMode(.inline)
    }
}*/

struct MainView_Previews: PreviewProvider {
    static var previews: some View {
        MainView(onNavigateToSubFirst: { _ in }, onNavigateToSubSecond: {})
    }
}
