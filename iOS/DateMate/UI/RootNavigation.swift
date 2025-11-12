//
//  RootNavigation.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/12.
//

import SwiftUI

struct RootNavigation: View {
    @State private var path: [String] = []

    var body: some View {
        NavigationStack(path: $path) {
            MainView2(onNavigate: { route in
                path.append(route)
            })
            .navigationDestination(for: String.self) { route in
                switch route {
                case let str where str.starts(with: "sub_first/"):
                    let data = str.replacingOccurrences(of: "sub_first/", with: "")
                    SubFirstView(viewModel: SubFirstViewModel(data: data))
                case "sub_second":
                    SubSecondView()
                default:
                    EmptyView()
                }
            }
        }
    }
}

struct RootNavigation_Previews: PreviewProvider {
    static var previews: some View {
        RootNavigation()
    }
}
